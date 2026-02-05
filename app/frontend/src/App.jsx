import { useEffect, useMemo, useState } from 'react'
import { BrowserRouter, Navigate, Route, Routes, useLocation, useNavigate } from 'react-router-dom'
import './App.css'

const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8080'

const getAccessToken = () => localStorage.getItem('accessToken')

function LoginPage({ onLogin }) {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [tokenInfo, setTokenInfo] = useState(null)
  const [signupMessage, setSignupMessage] = useState(false)
  const navigate = useNavigate()
  const location = useLocation()

  useEffect(() => {
    if (location.state?.signupSuccess) {
      setSignupMessage(true)
      const timer = setTimeout(() => setSignupMessage(false), 3000)
      return () => clearTimeout(timer)
    }
    return undefined
  }, [location.state])

  const handleSubmit = async (event) => {
    event.preventDefault()
    setLoading(true)
    setError('')
    setTokenInfo(null)

    try {
      const response = await fetch(`${API_BASE}/api/auth/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email, password }),
      })

      if (!response.ok) {
        const message = response.status === 401 || response.status === 400
          ? 'Invalid email or password.'
          : 'Login failed. Please try again.'
        throw new Error(message)
      }

      const data = await response.json()
      localStorage.setItem('accessToken', data.accessToken)
      localStorage.setItem('refreshToken', data.refreshToken)
      setTokenInfo(data)
      onLogin()
      navigate('/posts')
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page">
      <main className="shell">
        <section className="intro">
          <p className="eyebrow">Codex Board</p>
          <h1>
            AI Coding Agents를 활용하여 만든
            <span className="accent"> 간단한 게시판</span>
          </h1>
          <div className="intro-list">
            <p className="lead">
              <span className="tag">OpenAI Codex</span> 활용
            </p>
            <p className="lead">
              작업 시간 <strong>5시간</strong>
            </p>
            <p className="lead">
              code 주소
              <span className="code-link">
                <code>https://github.com/daejin94/codex_board</code>
              </span>
            </p>
          </div>
          <div className="badge-list">
            <span>Access + Refresh</span>
            <span>Redis session store</span>
            <span>Role-based grants</span>
          </div>
          <div className="demo">
            <p className="demo-title">Demo 계정 안내</p>
            <p>이메일: <strong>user@example.com</strong></p>
            <p>비밀번호: <strong>password123</strong></p>
          </div>
        </section>

        <section className="card">
          <header>
            <h2>Welcome back</h2>
            <p>Log in with your email address and password.</p>
          </header>

          {signupMessage && (
            <div className="signup-success">회원 가입이 완료되었습니다.</div>
          )}

          <form onSubmit={handleSubmit} className="form">
            <label>
              Email
              <input
                type="email"
                name="email"
                value={email}
                onChange={(event) => setEmail(event.target.value)}
                placeholder="you@company.com"
                required
              />
            </label>

            <label>
              Password
              <input
                type="password"
                name="password"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
                placeholder="********"
                required
              />
            </label>

            {error && <p className="error">{error}</p>}
            {tokenInfo && (
              <div className="success">
                <p>Login successful.</p>
                <p className="small">Access token expires in {tokenInfo.expiresInSeconds}s.</p>
              </div>
            )}

            <button type="submit" disabled={loading}>
              {loading ? 'Signing in...' : 'Sign in'}
            </button>
          </form>

          <button type="button" className="link" onClick={() => navigate('/signup')}>
            회원 가입
          </button>

          <footer>
            <p>
              By continuing you agree to the security policy and session lifecycle
              configured for this workspace.
            </p>
          </footer>
        </section>
      </main>
    </div>
  )
}

function SignupPage() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [phone, setPhone] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const navigate = useNavigate()

  const handleSubmit = async (event) => {
    event.preventDefault()
    setLoading(true)
    setError('')

    try {
      const response = await fetch(`${API_BASE}/api/auth/signup`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email, password, phone }),
      })

      if (!response.ok) {
        throw new Error('회원 가입에 실패했습니다.')
      }

      navigate('/login', { state: { signupSuccess: true } })
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page">
      <main className="shell">
        <section className="intro">
          <p className="eyebrow">Codex Board</p>
          <h1>회원 가입을 시작해 주세요.</h1>
          <p className="lead">
            기본 정보를 입력하면 게시글 작성과 추천 기능을 사용할 수 있습니다.
          </p>
        </section>

        <section className="card">
          <header>
            <h2>회원 가입</h2>
            <p>필수 정보를 입력해 주세요.</p>
          </header>

          <form onSubmit={handleSubmit} className="form">
            <label>
              Email
              <input
                type="email"
                name="email"
                value={email}
                onChange={(event) => setEmail(event.target.value)}
                placeholder="you@company.com"
                required
              />
            </label>

            <label>
              Password
              <input
                type="password"
                name="password"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
                placeholder="********"
                required
              />
            </label>

            <label>
              Phone
              <input
                type="text"
                name="phone"
                value={phone}
                onChange={(event) => setPhone(event.target.value)}
                placeholder="010-0000-0000"
                required
              />
            </label>

            {error && <p className="error">{error}</p>}

            <button type="submit" disabled={loading}>
              {loading ? 'Signing up...' : 'Sign up'}
            </button>
          </form>

          <button type="button" className="link" onClick={() => navigate('/login')}>
            로그인으로 돌아가기
          </button>
        </section>
      </main>
    </div>
  )
}

function PostsPage({ onLogout }) {
  const [activeTab, setActiveTab] = useState('popular')
  const [title, setTitle] = useState('')
  const [content, setContent] = useState('')
  const [saveStatus, setSaveStatus] = useState('')
  const [posts, setPosts] = useState([])
  const [page, setPage] = useState(0)
  const [hasMore, setHasMore] = useState(true)
  const [isLoadingMore, setIsLoadingMore] = useState(false)
  const [popularPosts, setPopularPosts] = useState([])
  const [popularSort, setPopularSort] = useState('likes')
  const [popularDir, setPopularDir] = useState('desc')
  const [popularLoading, setPopularLoading] = useState(false)
  const [likedPosts, setLikedPosts] = useState({})
  const navigate = useNavigate()

  const handleUnauthorized = () => {
    onLogout()
    navigate('/login', { replace: true })
  }

  const handleSave = async () => {
    setSaveStatus('')
    try {
      const accessToken = getAccessToken()
      const response = await fetch(`${API_BASE}/api/posts`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${accessToken}`,
        },
        body: JSON.stringify({ title, content }),
      })

      if (response.status === 401) {
        handleUnauthorized()
        return
      }

      if (!response.ok) {
        throw new Error('게시글 저장에 실패했습니다.')
      }

      setSaveStatus('저장되었습니다.')
      setTitle('')
      setContent('')
      setActiveTab('general')
    } catch (err) {
      setSaveStatus(err.message)
    }
  }

  const renderMarkdown = (value) => {
    const escapeHtml = (text) =>
      text
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;')

    const lines = value.split('\n')
    const html = []
    let inList = false

    const closeList = () => {
      if (inList) {
        html.push('</ul>')
        inList = false
      }
    }

    const formatInline = (text) => {
      let result = text
      result = result.replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
      result = result.replace(/\*(.+?)\*/g, '<em>$1</em>')
      result = result.replace(/`([^`]+?)`/g, '<code>$1</code>')
      return result
    }

    lines.forEach((rawLine) => {
      const line = rawLine.trimEnd()
      if (!line) {
        closeList()
        html.push('<br />')
        return
      }

      if (line.startsWith('### ')) {
        closeList()
        html.push(`<h3>${formatInline(escapeHtml(line.slice(4)))}</h3>`)
        return
      }
      if (line.startsWith('## ')) {
        closeList()
        html.push(`<h2>${formatInline(escapeHtml(line.slice(3)))}</h2>`)
        return
      }
      if (line.startsWith('# ')) {
        closeList()
        html.push(`<h1>${formatInline(escapeHtml(line.slice(2)))}</h1>`)
        return
      }

      if (line.startsWith('- ')) {
        if (!inList) {
          html.push('<ul>')
          inList = true
        }
        html.push(`<li>${formatInline(escapeHtml(line.slice(2)))}</li>`)
        return
      }

      closeList()
      html.push(`<p>${formatInline(escapeHtml(line))}</p>`)
    })

    closeList()
    return html.join('')
  }

  const fetchPosts = async (nextPage = 0) => {
    if (isLoadingMore) return
    setIsLoadingMore(true)
    try {
      const accessToken = getAccessToken()
      const response = await fetch(`${API_BASE}/api/posts?page=${nextPage}&size=10`, {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      })
      if (response.status === 401) {
        handleUnauthorized()
        return
      }
      if (!response.ok) {
        throw new Error('게시글 목록을 불러오지 못했습니다.')
      }
      const data = await response.json()
      if (nextPage === 0) {
        setPosts(data)
      } else {
        setPosts((prev) => [...prev, ...data])
      }
      setLikedPosts((prev) => {
        const next = { ...prev }
        data.forEach((post) => {
          next[post.id] = Boolean(post.liked)
        })
        return next
      })
      setHasMore(data.length === 10)
      setPage(nextPage)
    } catch (err) {
      setSaveStatus(err.message)
    } finally {
      setIsLoadingMore(false)
    }
  }

  const fetchPopular = async () => {
    setPopularLoading(true)
    try {
      const accessToken = getAccessToken()
      const response = await fetch(
        `${API_BASE}/api/posts/popular?sort=${popularSort}&dir=${popularDir}&page=0&size=10`,
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        },
      )
      if (response.status === 401) {
        handleUnauthorized()
        return
      }
      if (!response.ok) {
        throw new Error('인기 게시글을 불러오지 못했습니다.')
      }
      const data = await response.json()
      setPopularPosts(data)
      setLikedPosts((prev) => {
        const next = { ...prev }
        data.forEach((post) => {
          next[post.id] = Boolean(post.liked)
        })
        return next
      })
    } catch (err) {
      setSaveStatus(err.message)
    } finally {
      setPopularLoading(false)
    }
  }

  const toggleLike = async (postId, scope = 'popular') => {
    try {
      const accessToken = getAccessToken()
      const response = await fetch(`${API_BASE}/api/posts/${postId}/like`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      })
      if (response.status === 401) {
        handleUnauthorized()
        return
      }
      if (!response.ok) {
        throw new Error('추천 처리에 실패했습니다.')
      }
      const data = await response.json()
      setLikedPosts((prev) => ({ ...prev, [postId]: data.liked }))
      if (scope === 'popular') {
        setPopularPosts((prev) =>
          prev.map((post) =>
            post.id === postId ? { ...post, likeCount: data.likeCount } : post,
          ),
        )
        fetchPopular()
      } else {
        setPosts((prev) =>
          prev.map((post) =>
            post.id === postId ? { ...post, likeCount: data.likeCount } : post,
          ),
        )
      }
    } catch (err) {
      setSaveStatus(err.message)
    }
  }

  useEffect(() => {
    if (activeTab !== 'general') return
    fetchPosts(0)
  }, [activeTab])

  useEffect(() => {
    if (activeTab !== 'popular') return
    fetchPopular()
  }, [activeTab, popularSort, popularDir])

  useEffect(() => {
    if (activeTab !== 'general') return
    const handleScroll = () => {
      if (!hasMore || isLoadingMore) return
      const nearBottom = window.innerHeight + window.scrollY >= document.body.offsetHeight - 200
      if (nearBottom) {
        fetchPosts(page + 1)
      }
    }
    window.addEventListener('scroll', handleScroll)
    return () => window.removeEventListener('scroll', handleScroll)
  }, [activeTab, hasMore, isLoadingMore, page])

  return (
    <div className="page">
      <main className="main-shell">
        <header className="main-header">
          <div>
            <p className="eyebrow">Codex Board</p>
            <h1>환영합니다.</h1>
            <p className="lead">로그인 후 메인 페이지입니다.</p>
          </div>
          <button className="ghost" type="button" onClick={onLogout}>
            로그아웃
          </button>
        </header>

        <nav className="tabs">
          <button
            type="button"
            className={activeTab === 'popular' ? 'active' : ''}
            onClick={() => setActiveTab('popular')}
          >
            인기 게시글
          </button>
          <button
            type="button"
            className={activeTab === 'general' ? 'active' : ''}
            onClick={() => setActiveTab('general')}
          >
            일반 게시글
          </button>
          <button
            type="button"
            className={activeTab === 'write' ? 'active' : ''}
            onClick={() => setActiveTab('write')}
          >
            게시글 작성
          </button>
        </nav>

        <section className="tab-panel">
          {activeTab === 'popular' && (
            <div className="popular">
              <div className="popular-controls">
                <div className="control">
                  <span>정렬 기준</span>
                  <button
                    type="button"
                    className={popularSort === 'likes' ? 'active' : ''}
                    onClick={() => setPopularSort('likes')}
                  >
                    추천수
                  </button>
                  <button
                    type="button"
                    className={popularSort === 'createdAt' ? 'active' : ''}
                    onClick={() => setPopularSort('createdAt')}
                  >
                    업로드 시간
                  </button>
                </div>
                <div className="control">
                  <span>정렬 방향</span>
                  <button
                    type="button"
                    className={popularDir === 'desc' ? 'active' : ''}
                    onClick={() => setPopularDir('desc')}
                  >
                    내림차순
                  </button>
                  <button
                    type="button"
                    className={popularDir === 'asc' ? 'active' : ''}
                    onClick={() => setPopularDir('asc')}
                  >
                    오름차순
                  </button>
                </div>
              </div>
              {popularLoading && <p className="loading">불러오는 중...</p>}
              {!popularLoading && popularPosts.length === 0 && (
                <p className="empty">추천수 20개 이상 게시글이 없습니다.</p>
              )}
              <div className="post-list">
                {popularPosts.map((post) => (
                  <article key={post.id} className="post-card">
                    <h3>{post.title}</h3>
                    <p className="meta">
                      {post.authorName || '작성자 없음'} · {new Date(post.createdAt).toLocaleString()}
                    </p>
                    <p className="snippet">{post.content.slice(0, 120)}</p>
                    <div className="like-row">
                      <span className="like-count">추천 {post.likeCount}</span>
                      <button
                        type="button"
                        className={`like ${likedPosts[post.id] ? 'liked' : ''}`}
                        onClick={() => toggleLike(post.id)}
                      >
                        추천
                      </button>
                    </div>
                  </article>
                ))}
              </div>
            </div>
          )}
          {activeTab === 'general' && (
            <div className="post-list">
              {posts.length === 0 && !isLoadingMore && (
                <p className="empty">게시글이 없습니다.</p>
              )}
              {posts.map((post) => (
                <article key={post.id} className="post-card">
                  <h3>{post.title}</h3>
                  <p className="meta">
                    {post.authorName || '작성자 없음'} · {new Date(post.createdAt).toLocaleString()}
                  </p>
                  <p className="snippet">{post.content.slice(0, 120)}</p>
                  <div className="like-row">
                    <span className="like-count">추천 {post.likeCount}</span>
                    <button
                      type="button"
                      className={`like ${likedPosts[post.id] ? 'liked' : ''}`}
                      onClick={() => toggleLike(post.id, 'general')}
                    >
                      추천
                    </button>
                  </div>
                </article>
              ))}
              {isLoadingMore && <p className="loading">불러오는 중...</p>}
              {!hasMore && posts.length > 0 && <p className="loading">마지막 게시글입니다.</p>}
            </div>
          )}
          {activeTab === 'write' && (
            <div className="editor">
              <div className="editor-input">
                <label className="field">
                  제목
                  <input
                    type="text"
                    value={title}
                    onChange={(event) => setTitle(event.target.value)}
                    placeholder="제목을 입력하세요."
                  />
                </label>
                <label className="field">
                  내용 (Markdown)
                  <textarea
                    value={content}
                    onChange={(event) => setContent(event.target.value)}
                    placeholder="# 제목\n- 목록\n**강조** *기울임*"
                    rows={12}
                  />
                </label>
                <button type="button" onClick={handleSave} disabled={!title || !content}>
                  저장
                </button>
                {saveStatus && <p className="save-status">{saveStatus}</p>}
              </div>
              <div className="editor-preview">
                <p className="preview-title">미리보기</p>
                <h2 className="preview-heading">{title || '제목 미리보기'}</h2>
                <div
                  className="preview-content"
                  dangerouslySetInnerHTML={{ __html: renderMarkdown(content) }}
                />
              </div>
            </div>
          )}
        </section>
      </main>
    </div>
  )
}

function ProtectedRoute({ isAuthenticated, children }) {
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />
  }
  return children
}

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(Boolean(getAccessToken()))

  const handleLogout = () => {
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    setIsAuthenticated(false)
  }

  const authValue = useMemo(() => isAuthenticated, [isAuthenticated])

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/posts" replace />} />
        <Route
          path="/login"
          element={<LoginPage onLogin={() => setIsAuthenticated(true)} />}
        />
        <Route path="/signup" element={<SignupPage />} />
        <Route
          path="/posts"
          element={(
            <ProtectedRoute isAuthenticated={authValue}>
              <PostsPage onLogout={handleLogout} />
            </ProtectedRoute>
          )}
        />
      </Routes>
    </BrowserRouter>
  )
}

export default App