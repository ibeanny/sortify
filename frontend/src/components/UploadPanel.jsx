function UploadPanel({ selectedFiles, onFileChange, onUpload, loading }) {
    return (
        <div className="upload-panel">
            <div className="file-section">
                <label htmlFor="file-upload" className="file-upload-label">
                    Choose Text Files
                </label>

                <input
                    id="file-upload"
                    type="file"
                    accept=".txt"
                    multiple
                    onChange={onFileChange}
                    className="file-input"
                />

                {selectedFiles.length > 0 && (
                    <div className="file-selection">
                        <p className="file-name">
                            Selected <span>{selectedFiles.length}</span> file{selectedFiles.length === 1 ? "" : "s"}
                        </p>

                        <ul className="file-list">
                            {selectedFiles.map((file) => (
                                <li key={`${file.name}-${file.lastModified}`}>{file.name}</li>
                            ))}
                        </ul>
                    </div>
                )}
            </div>

            <div className="upload-section">
                <button onClick={onUpload} disabled={loading}>
                    {loading ? (
                        <span className="loading-content">
                            <span className="spinner"></span>
                            Processing
                        </span>
                    ) : (
                        "Upload & Sort"
                    )}
                </button>
            </div>
        </div>
    );
}

export default UploadPanel;
