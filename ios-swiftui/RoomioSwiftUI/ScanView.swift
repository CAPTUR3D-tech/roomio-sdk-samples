import RoomioSDK
import SwiftUI

struct ScanView: View {
    let session: RoomioScanSession
    let onDone: (RoomioBundle?) -> Void

    @State private var message: String?

    var body: some View {
        ZStack {
            RoomioScanView(session: session)

            if !session.isShowingOnboarding {
                VStack {
                    Spacer()
                    if let message = message ?? failureMessage {
                        Text(message)
                            .padding(12)
                            .background(.regularMaterial, in: .rect(cornerRadius: 12))
                    }
                    Spacer()
                    if session.state == .idle {
                        Button("Start") { session.start() }
                            .buttonStyle(.borderedProminent)
                            .controlSize(.large)
                    }
                }
                .padding()
            }
        }
        .onAppear {
            session.startPreview()
        }
        .onChange(of: session.state) { _, state in
            switch state {
            case .finished: Task { await export() }
            case .cancelled: close(with: nil)
            default: break
            }
        }
    }

    private var failureMessage: String? {
        if case .failed(let error) = session.state { return error.message }
        return nil
    }

    private func export() async {
        do {
            close(with: try await session.export())
        } catch {
            message = error.localizedDescription
        }
    }

    private func close(with bundle: RoomioBundle?) {
        session.close()
        onDone(bundle)
    }
}
