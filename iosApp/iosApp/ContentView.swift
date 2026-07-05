import UIKit
import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea(.container)
            .ignoreKeyboardSafeAreaIfAvailable()
    }
}

extension View {
    @ViewBuilder
    func ignoreKeyboardSafeAreaIfAvailable() -> some View {
        if #available(iOS 16.0, *) {
            self.ignoresSafeArea(.keyboard)
        } else {
            self // Just return the view without the modifier on older iOS
        }
    }
}


