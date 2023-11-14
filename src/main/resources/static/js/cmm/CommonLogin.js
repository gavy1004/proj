const USERNAME_COOKIE_KEY = "saveId";

class CommonLogin{
    // class variables
    // 아이디 저장 체크박스
    saveIdCheckbox; 
    // 비밀번호 가리기/표시
    eyeIcon;
    // 아이디 element
    userIdInput;
    // 비밀번호 element
    inputpw;

    constructor() {
        this.saveIdCheckbox = document.getElementById("saveId");
        this.userIdInput = document.getElementById("loginId");
        this.eyeIcon = document.getElementById("eye-icon");
        this.inputpw = document.getElementById("loginPwd");

        this.initialize();
    }

    initialize() {
        const _this = this;
        let userid = this.getCookie(USERNAME_COOKIE_KEY);   // 쿠키 값 저장

        // 전에 아이디를 저장해서 처음 페이지 로딩 시, 아이디 입력 창에 값이 있을때
        if (userid) {
            this.saveIdCheckbox.checked = true;  // 아이디 저장 체크박스 체크 상태로 두기
            this.userIdInput.value = userid;     // 쿠키에 저장된 아이디가 있을 때만 입력 칸에 값 설정
        }

        this.saveIdCheckbox.addEventListener('change', (ev) => {
            if (!_this.saveIdCheckbox.checked) {
                _this.deleteCookie("saveId");   // 아이디 값 쿠키 삭제
            }
        })

        _this.eyeIcon.addEventListener('click', (ev) => {
            if (_this.inputpw.getAttribute('type') === 'password') {
                _this.inputpw.setAttribute('type', 'text');
                _this.eyeIcon.classList.replace("fa-eye-slash", "fa-eye");
            } else if (_this.inputpw.getAttribute('type') === 'text') {
                _this.inputpw.setAttribute('type', 'password');
                _this.eyeIcon.classList.replace("fa-eye", "fa-eye-slash");
            }
        });

        _this.inputpw.addEventListener("keyup", function (event) {
            if (event.key === 'Enter') {
                document.getElementById("loginBtn").click();
            }
        });
    }

    saveUsernameToCookie(){
        if(!this.saveIdCheckbox.checked) {
            return;
        }
        const username = this.userIdInput.value;
        this.setCookie(USERNAME_COOKIE_KEY, username, 7);   // 아이디 값 7일동안 쿠키 저장
    }

    // 쿠키 가져오기
    // 인자로 받은 쿠키 이름(cookieName)을 기반으로 쿠키 값 가져옴
    getCookie(cookieName) {
        // 쿠키 이름을 찾기 위해 전체 쿠키 값 호출
        cookieName = cookieName + '=';
        var cookieData = document.cookie;
        
        // 쿠키 값이 있는지 확인하고, 있다면 해당 쿠키 값을 추출하여 반환
        var start = cookieData.indexOf(cookieName);
        var cookieValue = '';
        if(start != -1){
            start += cookieName.length;
            var end = cookieData.indexOf(';', start);
            if(end == -1)end = cookieData.length;
            cookieValue = cookieData.substring(start, end);
        }
        // 이스케이프 처리된 쿠키 값을 원래 값으로 복원하여 반환
        return unescape(cookieValue);
    }

     // 쿠키 삭제
    // 인자로 받은 쿠키 이름(cookieName)을 기반으로 쿠키 삭제
    deleteCookie(cookieName){
        this.setCookie(USERNAME_COOKIE_KEY, '');
    }

    // 쿠키 저장
    // 인자로 받은 쿠키 이름(cookieName), 값(value), 유효 기간(exdays)을 기반으로 쿠키 저장
    setCookie(cookieName, value, exdays){
        // 현재 날짜를 기준으로 유효 기간 설정
        const exdate = new Date();
        exdate.setDate(exdate.getDate() + exdays);
         // 쿠키 값 특수 문자를 이스케이프 처리하고, 유효 기간을 설정하여 전체 쿠키 값을 생성
        const cookieValue = escape(value) + ((exdays==null) ? "" : "; expires=" + exdate.toGMTString());
        document.cookie = cookieName + "=" + cookieValue;
    }
}