import Foundation
import Vision

// Read-only OCR: confirm the captured app actually rendered its example data.
let request = VNRecognizeTextRequest()
request.recognitionLevel = .accurate
request.recognitionLanguages = ["en-US"]
let handler = VNImageRequestHandler(url: URL(fileURLWithPath: CommandLine.arguments[1]))
try handler.perform([request])
let text = (request.results ?? []).compactMap { $0.topCandidates(1).first?.string }.joined(separator: "\n")
let normalized = text.lowercased().filter { !$0.isWhitespace }
guard normalized.contains("collection"), normalized.contains("demo"), normalized.contains("49.00") else {
    fputs("Screenshot did not render Collection, Demo and its purchase value:\n\(text)\n", stderr)
    exit(1)
}
print(text)
