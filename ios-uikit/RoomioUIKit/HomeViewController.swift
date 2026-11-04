import RoomioSDK
import UIKit

final class HomeViewController: UIViewController {
    private var bundle: RoomioBundle?

    private let lastScanLabel = UILabel()
    private let shareButton = UIButton(configuration: .plain())
    private let diagnosticsButton = UIButton(configuration: .plain())
    private let errorLabel = UILabel()
    private let startButton = UIButton(configuration: .filled())

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .systemBackground

        let icon = UIImageView(image: UIImage(systemName: "cube.transparent"))
        icon.preferredSymbolConfiguration = UIImage.SymbolConfiguration(pointSize: 64)

        let title = UILabel()
        title.text = "Roomio Sample"
        title.font = .systemFont(ofSize: 34, weight: .bold)

        shareButton.configuration?.title = "Share scan"
        shareButton.addAction(UIAction { [weak self] _ in self?.shareScan() }, for: .primaryActionTriggered)

        diagnosticsButton.configuration?.title = "Send diagnostics"
        diagnosticsButton.addAction(UIAction { [weak self] _ in self?.sendDiagnostics() }, for: .primaryActionTriggered)

        errorLabel.textColor = .systemRed
        errorLabel.numberOfLines = 0
        errorLabel.textAlignment = .center

        let header = UIStackView(arrangedSubviews: [icon, title, lastScanLabel, shareButton, diagnosticsButton, errorLabel])
        header.axis = .vertical
        header.alignment = .center
        header.spacing = 20

        let footer: UIView
        if Roomio.isSupported() == .supported {
            startButton.configuration?.title = "Start scan"
            startButton.configuration?.buttonSize = .large
            startButton.addAction(UIAction { [weak self] _ in self?.startScan() }, for: .primaryActionTriggered)
            footer = startButton
        } else {
            let unsupported = UILabel()
            unsupported.text = "This device can't scan."
            unsupported.textColor = .secondaryLabel
            footer = unsupported
        }

        header.translatesAutoresizingMaskIntoConstraints = false
        footer.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(header)
        view.addSubview(footer)
        NSLayoutConstraint.activate([
            header.centerYAnchor.constraint(equalTo: view.centerYAnchor),
            header.leadingAnchor.constraint(equalTo: view.layoutMarginsGuide.leadingAnchor),
            header.trailingAnchor.constraint(equalTo: view.layoutMarginsGuide.trailingAnchor),
            footer.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            footer.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor, constant: -20),
        ])

        render()
    }

    private func render() {
        lastScanLabel.text = bundle.map { "Last scan: \($0.sizeBytes.formatted(.byteCount(style: .file)))" }
        lastScanLabel.isHidden = bundle == nil
        shareButton.isHidden = bundle == nil
        errorLabel.isHidden = errorLabel.text == nil
    }

    private func startScan() {
        errorLabel.text = nil
        do {
            let session = try Roomio.startScan()
            let scan = ScanViewController(session: session) { [weak self] result in
                self?.dismiss(animated: true)
                if let result { self?.bundle = result }
                self?.render()
            }
            scan.modalPresentationStyle = .fullScreen
            present(scan, animated: true)
        } catch {
            show(error: error.localizedDescription)
        }
    }

    private func show(error message: String) {
        errorLabel.text = message
        render()
    }

    private func shareScan() {
        guard let bundle else { return }
        share(bundle.fileURL, from: shareButton)
    }

    private func sendDiagnostics() {
        Task {
            do {
                share(try await Roomio.exportDiagnostics(), from: diagnosticsButton)
            } catch {
                show(error: error.localizedDescription)
            }
        }
    }

    private func share(_ url: URL, from source: UIView) {
        let activity = UIActivityViewController(activityItems: [url], applicationActivities: nil)
        activity.popoverPresentationController?.sourceView = source
        present(activity, animated: true)
    }
}
