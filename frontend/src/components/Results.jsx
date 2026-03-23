function Results({ data, onDownloadTxt }) {
    if (!data) return null;

    const simplifiedJson = {
        combinedCategories: data.combinedCategories,
        totalFiles: data.totalFiles,
        totalLines: data.totalLines,
    };

    return (
        <div className="response-box">
            <h2>Results</h2>

            <div className="stats">
                <div className="stat-card">
                    <span className="stat-label">Files</span>
                    <span className="stat-value">{data.totalFiles}</span>
                </div>

                <div className="stat-card">
                    <span className="stat-label">Lines</span>
                    <span className="stat-value">{data.totalLines}</span>
                </div>
            </div>

            {data.combinedCategories?.length > 0 && (
                <div className="categories-section">
                    <h3>Combined Categories</h3>
                    <div className="category-grid">
                        {data.combinedCategories.map((group, index) => (
                            <div className="category-card" key={index}>
                                <h4>{group.category}</h4>
                                <ul>
                                    {group.values.map((value, i) => (
                                        <li key={i}>{value}</li>
                                    ))}
                                </ul>
                            </div>
                        ))}
                    </div>
                </div>
            )}

            <div className="results-actions">
                <button onClick={onDownloadTxt}>Download TXT</button>
            </div>

            <details className="raw-json">
                <summary>Show raw JSON</summary>
                <pre>{JSON.stringify(simplifiedJson, null, 2)}</pre>
            </details>
        </div>
    );
}

export default Results;