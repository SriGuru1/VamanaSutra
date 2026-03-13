import { useState } from 'react'
import './App.css'

function App() {
  const [longUrl, setLongUrl] = useState('')
  const [shortUrl, setShortUrl] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!longUrl) return

    setLoading(true)
    setError('')
    setShortUrl('')

    try {
      const response = await fetch('/api/url-shortener', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ longUrl }),
      })

      let data;
      try {
        data = await response.json()
      } catch (e) {
        throw new Error('Server returned an invalid response (is the backend running?)')
      }

      if (!response.ok) {
        throw new Error(data?.Error || 'Failed to shorten URL')
      }

      setShortUrl(data.shortUrl)
    } catch (err: any) {
      setError(err.message || 'An unexpected error occurred')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="container">
      <header className="header">
        <h1>URL Shortener</h1>
        <p>Shorten your long links into easy to share, concise URLs.</p>
      </header>
      
      <main className="main-content">
        <form onSubmit={handleSubmit} className="shortener-form">
          <input
            type="url"
            placeholder="Enter your long URL here (e.g. https://example.com/very/long/path)"
            value={longUrl}
            onChange={(e) => setLongUrl(e.target.value)}
            required
            className="url-input"
          />
          <button type="submit" disabled={loading} className="submit-btn">
            {loading ? 'Shortening...' : 'Shorten URL'}
          </button>
        </form>

        {error && (
          <div className="error-message">
            <p>{error}</p>
          </div>
        )}

        {shortUrl && (
          <div className="result-container">
            <h2>Your shortened URL:</h2>
            <div className="short-url-box">
              <a href={shortUrl} target="_blank" rel="noopener noreferrer">
                {shortUrl}
              </a>
              <button 
                onClick={() => navigator.clipboard.writeText(shortUrl)}
                className="copy-btn"
                title="Copy to clipboard"
              >
                Copy
              </button>
            </div>
          </div>
        )}
      </main>
    </div>
  )
}

export default App
