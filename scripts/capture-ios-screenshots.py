#!/usr/bin/env python3
"""Capture the existing app on fresh iPhone/iPad simulators; never resize PNGs."""
import json
import os
from pathlib import Path
import platform
import plistlib
import shutil
import struct
import subprocess
import time
from zipfile import ZipFile, ZIP_DEFLATED

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / 'build/store-screenshots'
BUNDLE_ID = 'com.lyheang.ash'


def run(*args, capture=False, timeout=300):
    print('+', ' '.join(map(str, args)), flush=True)
    return subprocess.run(list(map(str, args)), check=True, timeout=timeout,
                          text=True, capture_output=capture).stdout


def sim(*args, **kwargs):
    return run('xcrun', 'simctl', *args, **kwargs)


def png_size(path):
    header = path.read_bytes()[:24]
    if header[:8] != b'\x89PNG\r\n\x1a\n' or header[12:16] != b'IHDR':
        raise ValueError(f'Not a PNG screenshot: {path}')
    return struct.unpack('>II', header[16:24])


def seed_demo(container):
    image_dir = container / 'Library/Application Support/AssetImages'
    image_dir.mkdir(parents=True, exist_ok=True)
    shutil.copy2(ROOT / 'iosApp/iosApp/Assets.xcassets/AppIcon.appiconset/app-icon-1024.png',
                 image_dir / 'store-demo.png')
    # Existing AssetEntity serialization and ash-image URI; no app code changes.
    asset = dict(id='store-demo', name='Demo', category='PHONE', brand='', model='',
                 serialNumber=None, purchaseDate=None, purchasePrice=49.0,
                 currentEstimatedValue=49.0, condition='GOOD', ownershipStatus='OWNED',
                 warrantyEndDate=None, imageUris=['ash-image:store-demo.png'], notes='',
                 maintenanceRecords=[], isInUse=True, createdAt='2026-10-08',
                 updatedAt='2026-10-08', tags=[], specialDetails='', willingToSell=False,
                 desiredSellingPrice=None, reminders=[])
    prefs = container / 'Library/Preferences' / f'{BUNDLE_ID}.plist'
    prefs.parent.mkdir(parents=True, exist_ok=True)
    prefs.write_bytes(plistlib.dumps({'ash.assets.v1': json.dumps([asset])}))


def capture_device(device_type, runtime, app, family):
    udid = sim('create', f'AshStore-{family}', device_type['identifier'],
               runtime['identifier'], capture=True).strip()
    try:
        sim('boot', udid)
        sim('bootstatus', udid, '-b', timeout=300)
        sim('ui', udid, 'appearance', 'light')
        sim('status_bar', udid, 'override', '--time', '9:41', '--dataNetwork', 'wifi',
            '--wifiMode', 'active', '--wifiBars', '3', '--cellularMode', 'active',
            '--cellularBars', '4', '--batteryState', 'charged', '--batteryLevel', '100')
        sim('install', udid, app)
        container = Path(sim('get_app_container', udid, BUNDLE_ID, 'data', capture=True).strip())
        seed_demo(container)
        sim('launch', udid, BUNDLE_ID)
        time.sleep(15)  # First Compose frame and selected image must be rendered.
        path = OUT / 'app-store' / f'{family}-collection.png'
        sim('io', udid, 'screenshot', '--type=png', path)
        size = png_size(path)
        recognized = run('swift', ROOT / 'scripts/verify-store-screenshot.swift', path,
                         capture=True, timeout=120).strip()
        allowed = {'iphone': {(1320, 2868), (1290, 2796)},
                   'ipad': {(2064, 2752)}}
        if size not in allowed[family]:
            raise ValueError(f'Unexpected native {family} screenshot size: {size}')
        prefs = container / 'Library/Preferences' / f'{BUNDLE_ID}.plist'
        if prefs.exists() and 'ash.assets.v1.recovery' in plistlib.loads(prefs.read_bytes()):
            raise ValueError('The app rejected its screenshot example data')
        return dict(file=str(path.relative_to(OUT)), device=device_type['name'],
                    runtime=runtime['name'], pixels=list(size), source='simctl screenshot',
                    recognized_text=recognized)
    finally:
        subprocess.run(['xcrun', 'simctl', 'shutdown', udid], check=False, timeout=60)
        subprocess.run(['xcrun', 'simctl', 'delete', udid], check=False, timeout=60)


def main():
    if platform.system() != 'Darwin' or platform.machine() != 'arm64':
        raise SystemExit('iOS screenshot capture requires macOS arm64 with Xcode.')
    os.chdir(ROOT)
    if os.environ.get('JAVA_HOME'):
        os.environ['ASH_JAVA_HOME'] = os.environ['JAVA_HOME']
    runtimes = json.loads(sim('list', 'runtimes', '--json', capture=True))['runtimes']
    available = [r for r in runtimes if r.get('isAvailable')
                 and '.iOS-' in r['identifier']]
    if not available:
        raise SystemExit('No available iOS simulator runtime is installed.')
    runtime = max(available, key=lambda r: tuple(int(v) for v in r['version'].split('.')))
    types = json.loads(sim('list', 'devicetypes', '--json', capture=True))['devicetypes']
    choices = [('iphone', ['iPhone 17 Pro Max', 'iPhone 16 Pro Max']),
               ('ipad', ['iPad Pro 13-inch (M5)', 'iPad Pro 13-inch (M4)'])]
    devices = []
    for family, names in choices:
        match = next((d for n in names for d in types if d['name'] == n), None)
        if not match:
            raise SystemExit(f'Missing required {family} simulator type: {names}')
        devices.append((family, match))
    OUT.mkdir(parents=True, exist_ok=True)
    for family in ('app-store', 'google-play'):
        (OUT / family).mkdir(exist_ok=True)
    log = OUT / 'ios-simulator-build.log'
    command = ['xcodebuild', '-project', 'iosApp/iosApp.xcodeproj', '-scheme', 'iosApp',
               '-configuration', 'Debug', '-sdk', 'iphonesimulator', '-destination',
               'generic/platform=iOS Simulator', '-derivedDataPath', 'iosApp/build/ScreenshotDerivedData',
               'ARCHS=arm64', 'ONLY_ACTIVE_ARCH=YES', 'CODE_SIGNING_ALLOWED=NO', 'build']
    print('Building the existing arm64 simulator app', flush=True)
    with log.open('w') as stream:
        result = subprocess.run(command, stdout=stream, stderr=subprocess.STDOUT, timeout=1800)
    if result.returncode:
        tail = '\n'.join(log.read_text(errors='replace').splitlines()[-70:])
        escaped = tail.replace('%', '%25').replace('\r', '%0D').replace('\n', '%0A')
        print(f'::error::iOS simulator build failed: {escaped}', flush=True)
        raise SystemExit(result.returncode)
    app = ROOT / 'iosApp/build/ScreenshotDerivedData/Build/Products/Debug-iphonesimulator/Ash.app'
    if not app.is_dir():
        raise SystemExit(f'Simulator build did not produce {app}')
    captures = [capture_device(device, runtime, app, family) for family, device in devices]
    for src, name in [('03-demo-collection.png', '01-collection.png'),
                      ('02-privacy-policy.png', '02-privacy-policy.png')]:
        target = OUT / 'google-play' / name
        shutil.copy2(ROOT / 'docs/release/screenshots/android' / src, target)
        captures.append(dict(file=str(target.relative_to(OUT)), pixels=list(png_size(target)),
                             device='Android 29 x86_64 emulator', source='adb screencap'))
    metadata = dict(commit=run('git', 'rev-parse', 'HEAD', capture=True).strip(),
                    xcode=run('xcodebuild', '-version', capture=True).strip(),
                    configuration='Debug iOS simulator; optimized Android emulator',
                    data='Synthetic Demo asset with $49 purchase value and the existing Ash icon',
                    captures=captures)
    (OUT / 'capture.json').write_text(json.dumps(metadata, indent=2) + '\n')
    (OUT / 'README.md').write_text(
        '# Ash store screenshots\n\n'
        'Four actual app screenshots: two Android, one iPhone, and one 13-inch iPad.\n'
        'These are native PNG captures, without generated UI, device frames or resizing.\n'
        'The Demo/$49 example is synthetic; its selected image is the existing Ash icon.\n'
        'See capture.json for devices, native pixel sizes, build and source revision.\n'
        'Review the images and current store-console display requirements before uploading.\n')
    archive = OUT.parent / 'ash-store-screenshots.zip'
    with ZipFile(archive, 'w', ZIP_DEFLATED) as z:
        for path in sorted(OUT.rglob('*')):
            if path.is_file() and path.suffix in ('.png', '.json', '.md'):
                z.write(path, path.relative_to(OUT))
    with ZipFile(archive) as z:
        if z.testzip() or len([n for n in z.namelist() if n.endswith('.png')]) != 4:
            raise ValueError('Screenshot package failed integrity/count validation')
    print('Captured and packaged four real app screenshots', flush=True)


if __name__ == '__main__':
    main()
