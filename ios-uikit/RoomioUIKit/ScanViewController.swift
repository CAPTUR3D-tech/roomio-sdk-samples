import Observation
import RoomioSDK
import SwiftUI
import UIKit

final class ScanViewController: UIViewController {
    private let session: RoomioScanSession
    private let onDone: (RoomioBundle?) -> Void

    private var message: String?
    private var export: Task<Void, Never>?
    private var closed = false

    private let messageLabel = UILabel()
    private lazy var messagePanel = material(around: messageLabel)

    init(session: RoomioScanSession, onDone: @escaping (RoomioBundle?) -> Void) {
        self.session = session
        self.onDone = onDone
        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        let scanView = UIHostingController(rootView: RoomioScanView(session: session))
        addChild(scanView)
        scanView.view.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(scanView.view)
        scanView.didMove(toParent: self)

        messageLabel.numberOfLines = 0
        messageLabel.textAlignment = .center

        messagePanel.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(messagePanel)

        NSLayoutConstraint.activate([
            scanView.view.topAnchor.constraint(equalTo: view.topAnchor),
            scanView.view.bottomAnchor.constraint(equalTo: view.bottomAnchor),
            scanView.view.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            scanView.view.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            messagePanel.centerYAnchor.constraint(equalTo: view.centerYAnchor),
            messagePanel.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 16),
            messagePanel.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -16),
        ])

        session.start()
        observeState()
    }

    private func observeState() {
        withObservationTracking {
            render()
        } onChange: { [weak self] in
            Task { @MainActor in self?.observeState() }
        }
    }

    private func render() {
        let state = session.state
        let covered = session.isShowingOnboarding || session.isShowingDialog

        var text = message
        if case .failed(let error) = state { text = error.message }
        messageLabel.text = text
        messagePanel.isHidden = covered || text == nil

        switch state {
        case .finished where export == nil:
            export = Task { await exportScan() }
        case .cancelled:
            close(with: nil)
        default:
            break
        }
    }

    private func exportScan() async {
        do {
            close(with: try await session.export())
        } catch {
            message = error.localizedDescription
            render()
        }
    }

    private func close(with bundle: RoomioBundle?) {
        guard !closed else { return }
        closed = true
        session.close()
        onDone(bundle)
    }

    private func material(around content: UIView) -> UIView {
        let background = UIVisualEffectView(effect: UIBlurEffect(style: .systemMaterial))
        background.layer.cornerRadius = 20
        background.clipsToBounds = true
        content.translatesAutoresizingMaskIntoConstraints = false
        background.contentView.addSubview(content)
        NSLayoutConstraint.activate([
            content.topAnchor.constraint(equalTo: background.contentView.topAnchor, constant: 12),
            content.bottomAnchor.constraint(equalTo: background.contentView.bottomAnchor, constant: -12),
            content.leadingAnchor.constraint(equalTo: background.contentView.leadingAnchor, constant: 16),
            content.trailingAnchor.constraint(equalTo: background.contentView.trailingAnchor, constant: -16),
        ])
        return background
    }
}
