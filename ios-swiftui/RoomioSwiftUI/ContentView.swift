import RoomioSDK
import SwiftUI

struct ContentView: View {
    @State private var session: RoomioScanSession?
    @State private var bundle: RoomioBundle?
    @State private var errorMessage: String?
    @State private var diagnostics: SharedFile?

    var body: some View {
        if let session {
            ScanView(session: session) { result in
                bundle = result ?? bundle
                self.session = nil
            }
        } else {
            home
        }
    }

    private var home: some View {
        VStack(spacing: 20) {
            Spacer()
            Image(systemName: "cube.transparent")
                .font(.system(size: 64))
                .foregroundStyle(.tint)
            Text("Roomio Sample")
                .font(.largeTitle.bold())

            if let bundle {
                VStack(spacing: 8) {
                    Text("Last scan: \(bundle.sizeBytes.formatted(.byteCount(style: .file)))")
                    ShareLink("Share scan", item: bundle.fileURL)
                }
            }

            Button("Send diagnostics", action: exportDiagnostics)

            if let errorMessage {
                Text(errorMessage)
                    .foregroundStyle(.red)
                    .multilineTextAlignment(.center)
            }

            Spacer()

            if Roomio.isSupported() == .supported {
                Button("Start scan", action: startScan)
                .buttonStyle(.borderedProminent)
                .controlSize(.large)
            } else {
                Text("This device can't scan.")
                    .foregroundStyle(.secondary)
            }
        }
        .padding()
        .sheet(item: $diagnostics) { ShareSheet(url: $0.url) }
    }

    private func exportDiagnostics() {
        Task {
            do {
                diagnostics = SharedFile(url: try await Roomio.exportDiagnostics())
            } catch {
                errorMessage = error.localizedDescription
            }
        }
    }

    private func startScan() {
        errorMessage = nil
        do {
            session = try Roomio.startScan()
        } catch {
            errorMessage = error.localizedDescription
        }
    }
}

private struct SharedFile: Identifiable {
    let url: URL
    var id: URL { url }
}

/// The system share sheet, opened as soon as the file exists.
private struct ShareSheet: UIViewControllerRepresentable {
    let url: URL

    func makeUIViewController(context: Context) -> UIActivityViewController {
        UIActivityViewController(activityItems: [url], applicationActivities: nil)
    }

    func updateUIViewController(_ controller: UIActivityViewController, context: Context) {}
}
