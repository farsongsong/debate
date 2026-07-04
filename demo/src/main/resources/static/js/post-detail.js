// CSRF
function getCsrfToken() {
    const meta = document.querySelector('meta[name="_csrf"]');
    const header = document.querySelector('meta[name="_csrf_header"]');
    if (meta && header) return { token: meta.content, header: header.content };
    return null;
}

function authFetch(url, options = {}) {
    const csrf = getCsrfToken();
    const headers = { 'Content-Type': 'application/json', ...options.headers };
    if (csrf) headers[csrf.header] = csrf.token;
    return fetch(url, { ...options, headers });
}

// 찬성/반대 선택
let selectedSide = 'AGREE';
function selectSide(side) {
    selectedSide = side;
    document.querySelectorAll('.side-btn').forEach(b => {
        b.className = 'side-btn';
        if (b.dataset.side === side) {
            if (side === 'AGREE') b.classList.add('active-agree');
            else if (side === 'DISAGREE') b.classList.add('active-disagree');
            else b.classList.add('active-neutral');
        }
    });
}

// 댓글 탭 필터
let currentTab = 'ALL';
function switchTab(tab) {
    currentTab = tab;
    document.querySelectorAll('.comment-tab').forEach(t => {
        t.className = 'comment-tab';
        if (t.dataset.tab === tab) {
            if (tab === 'ALL') t.classList.add('active-all');
            else if (tab === 'AGREE') t.classList.add('active-agree');
            else t.classList.add('active-disagree');
        }
    });
    document.querySelectorAll('.comment-item[data-side]').forEach(item => {
        item.style.display = (tab === 'ALL' || item.dataset.side === tab) ? '' : 'none';
    });
}

async function submitComment(parentId = null) {
    const contentEl = parentId
        ? document.getElementById('reply-content-' + parentId)
        : document.getElementById('commentContent');
    if (!contentEl) return alert('댓글 입력창을 찾을 수 없습니다.');
    const content = contentEl.value.trim();
    if (!content) return alert('내용을 입력해주세요.');

    try {
        const res = await authFetch('/api/posts/' + POST_ID + '/comments', {
            method: 'POST',
            body: JSON.stringify({
                content: content,
                parentId: parentId,
                side: parentId ? 'NEUTRAL' : selectedSide
            })
        });
        const data = await res.json();
        if (data.success) {
            contentEl.value = '';
            if (parentId) {
                const form = document.getElementById('reply-form-' + parentId);
                if (form) form.style.display = 'none';
            }
            location.reload();
        } else {
            alert(data.message || '댓글 등록 실패');
        }
    } catch (e) {
        console.error(e);
        alert('오류가 발생했습니다.');
    }
}

function submitReply(parentId) {
    submitComment(parentId);
}

function toggleReply(commentId) {
    const form = document.getElementById('reply-form-' + commentId);
    if (form) form.style.display = form.style.display === 'none' ? 'block' : 'none';
}

async function deleteComment(commentId) {
    if (!confirm('댓글을 삭제하시겠습니까?')) return;
    try {
        const res = await authFetch('/api/posts/' + POST_ID + '/comments/' + commentId, {
            method: 'DELETE'
        });
        const data = await res.json();
        if (data.success) {
            const el = document.getElementById('comment-' + commentId);
            if (el) el.remove();
        } else {
            alert(data.message);
        }
    } catch (e) {
        alert('오류가 발생했습니다.');
    }
}

// 좋아요
const likeBtn = document.getElementById('likeBtn');
if (likeBtn) {
    likeBtn.addEventListener('click', async () => {
        try {
            const res = await authFetch('/api/posts/' + likeBtn.dataset.postId + '/like', {
                method: 'POST'
            });
            const data = await res.json();
            if (data.success) {
                document.getElementById('likeCount').textContent = data.data.count;
                likeBtn.classList.toggle('liked', data.data.liked);
            }
        } catch (e) {
            console.error(e);
        }
    });
}

// 투표
async function sendVote(voteType) {
    try {
        const res = await authFetch('/api/posts/' + POST_ID + '/vote', {
            method: 'POST',
            body: JSON.stringify({ voteType: voteType })
        });
        const data = await res.json();
        if (data.success) {
            const r = data.data;
            document.querySelector('.vote-bar-agree').style.width = r.agreePercent + '%';
            document.querySelector('.vote-bar-disagree').style.width = r.disagreePercent + '%';
            document.querySelector('.vote-stat-agree').textContent = '👍 찬성 ' + r.agreePercent + '%';
            document.querySelector('.vote-stat-disagree').textContent = '반대 ' + r.disagreePercent + '% 👎';
            document.querySelector('.vote-stat-total').textContent = r.total + '명 참여';
            document.getElementById('voteAgree').classList.toggle('voted', voteType === 'AGREE');
            document.getElementById('voteDisagree').classList.toggle('voted', voteType === 'DISAGREE');
            const agreeCount = document.querySelector('#voteAgree .vote-count');
            const disagreeCount = document.querySelector('#voteDisagree .vote-count');
            if (agreeCount) agreeCount.textContent = r.agree;
            if (disagreeCount) disagreeCount.textContent = r.disagree;
        }
    } catch (e) {
        console.error(e);
    }
}

document.getElementById('voteAgree')?.addEventListener('click', () => sendVote('AGREE'));
document.getElementById('voteDisagree')?.addEventListener('click', () => sendVote('DISAGREE'));

// 사이드 버튼 이벤트
document.querySelectorAll('.side-btn').forEach(b => {
    b.addEventListener('click', () => selectSide(b.dataset.side));
});

// 탭 이벤트
document.querySelectorAll('.comment-tab').forEach(t => {
    t.addEventListener('click', () => switchTab(t.dataset.tab));
});

// 초기화
selectSide('AGREE');