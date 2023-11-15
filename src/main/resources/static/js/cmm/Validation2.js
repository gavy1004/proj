
const USER_ID_REGEX = /^[a-z]+[a-z0-9._-]{3,19}$/;
const PASSWORD_REGEX = /^.*(?=^.{9,20}$)(?=.*\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&+=*]).*$/;
const MOBILE_NUMBER_REGEX = /^01([0|1|6|7|8|9]?)?([0-9]{3,4})?([0-9]{4})$/;

class Validation {
    // class variables
    hasUserIdUniqueCheck = false;
    hasEmailUniqueCheck = false;
    isPasswordValid = false;
    isPasswordsEqual = false;
    hasEmailValidated = false;

    joinModal;
    joinModalCanBtn;

    idInput;        // ID 
    idDupCheckBtn;  // ID 중복 체크 
    idHelpMessage;
    passwordInput;
    passwordCheckInput;
    passwordHelpMessage;
    passwordHelpMessage2;

    nameInput;
    emailInput;
    emailConfirmBtn;
    emailConfirmDiv;
    emailConfirmInput;
    emailConfirmNumberBtn;
    emailConfirmHelpMessage;
    emailReceptionAgreeCheckbox;

    constructor() {
        this.idInput = document.getElementById("joinId");
        this.idDupCheckBtn = document.getElementById("idDupCheckBtn");
        this.idHelpMessage = document.getElementById("idHelpMessage");
        this.passwordInput = document.getElementById("joinPwd");
        this.passwordCheckInput = document.getElementById("joinPwdChk");
        this.passwordHelpMessage = document.getElementById("passwordHelpMessage");
        this.passwordHelpMessage2 = document.getElementById("passwordHelpMessage2");
        this.emailInput = document.getElementById("joinMail");
        this.emailConfirmBtn = document.getElementById("emailConfirmBtn");

        this.emailConfirmDiv = document.getElementById("emailConfirmDiv");
        this.emailConfirmInput = document.getElementById("emailConfirmInput");
        this.emailConfirmNumberBtn = document.getElementById("emailConfirmNumberBtn");
        this.emailConfirmHelpMessage = document.getElementById("emailConfirmHelpMessage");
        this.emailReceptionAgreeCheckbox = document.getElementById("emailReceptionAgreeCheckbox");
        this.joinModal = document.getElementById("joinModal");
        this.joinModalCanBtn = document.getElementById("joinModalCanBtn");

        this.initValidator();
        this.initUserIdCheck(); // ID 확인
        this.initInfoInputs();  // 인풋 입력
    }

    init() {
        const _this = this;
        CustomAlert.confirm("창을 닫으시겠습니까? 창을 닫으면 입력내용이 초기화 됩니다.",function(){
            $(_this.joinModal).modal('hide')	
    
            _this.hasUserIdUniqueCheck = false;
            _this.hasEmailUniqueCheck = false;
            _this.isPasswordValid = false;
            _this.isPasswordsEqual = false;
            _this.hasEmailValidated = false;
    
            document.forms['joinForm'].reset(); 
    
            _this.idHelpMessage.innerText = "";
            _this.passwordHelpMessage.innerText = "";
            _this.passwordHelpMessage2.innerText = "";
    
            _this.idInput.classList.remove("input-is-invalid");
            _this.idInput.classList.remove("input-is-valid");
            _this.passwordInput.classList.remove("input-is-invalid");
            _this.passwordInput.classList.remove("input-is-valid");
            _this.passwordCheckInput.classList.remove("input-is-invalid");
            _this.passwordCheckInput.classList.remove("input-is-valid");
            _this.emailConfirmDiv.style.display = 'none';

            /*this.emailConfirmDiv = document.getElementById("emailConfirmDiv");
            this.emailConfirmInput = document.getElementById("emailConfirmInput");
            this.emailConfirmNumberBtn = document.getElementById("emailConfirmNumberBtn");
            this.emailConfirmHelpMessage = document.getElementById("emailConfirmHelpMessage");
            this.emailReceptionAgreeCheckbox = document.getElementById("emailReceptionAgreeCheckbox");*/
        });
    }
    initValidator() {
        const _this = this;

        this.emailConfirmBtn.addEventListener('click', async (ev) => {
            await _this.sendEmailConfirmNumber();
        })

        this.joinModalCanBtn.addEventListener('click', async (ev) => {
            await _this.init();
        })
    }

    initInfoInputs() {
        const _this = this;

        _this.idInput.addEventListener('keyup', (ev) =>{
            const inputId = ev.target.value;
            const regexResult = USER_ID_REGEX.test(inputId);
            if(regexResult) {
                _this.idHelpMessage.innerText = "";
            } else {
                _this.idHelpMessage.classList.add("text-primary");
                _this.idHelpMessage.classList.remove("text-danger");
                _this.idHelpMessage.innerText = "ⓘ 4-20자 이내 영문 소문자, 숫자만 가능합니다.";
            }
            _this.idInput.classList.remove("input-is-invalid");
            _this.idInput.classList.remove("input-is-valid");
        })

        _this.passwordInput.addEventListener("keyup", (ev) => {
            const inputPassword = ev.target.value;
            const regexResult = PASSWORD_REGEX.test(inputPassword);
            if(regexResult) {
                _this.passwordInput.classList.add("input-is-valid");
                _this.passwordInput.classList.remove("input-is-invalid");
                _this.passwordHelpMessage.innerText = "";
                _this.isPasswordValid = true;
            } else {
                _this.passwordInput.classList.add("input-is-invalid");
                _this.passwordInput.classList.remove("input-is-valid");
                _this.passwordHelpMessage.classList.add("text-danger");
                _this.passwordHelpMessage.innerText = "ⓘ 반복, 연속문자, 개인정보 관련은 사용불가합니다." +
                    "숫자, 영문자,특수문자(!,@,#,$,%,^,&,+,=,~, *) 조합 9자 이상~20자 이하";
                _this.isPasswordValid = false;
            }
            _this.checkPasswordEquals();
        })

        _this.passwordCheckInput.addEventListener("keyup", (ev) => {
            _this.checkPasswordEquals();
        })
    }

    initUserIdCheck() {
        const _this = this;
        _this.idInput.addEventListener('keyup', (ev) => {
            if(_this.hasUserIdUniqueCheck) {
                _this.hasUserIdUniqueCheck = false;
            }
        })

        _this.idDupCheckBtn.addEventListener('click', async (ev) => {
            const userId = _this.idInput.value;
            if (!USER_ID_REGEX.test(userId) === true) {
                _this.idInput.setCustomValidity("영문으로 시작하는 영문, 숫자 조합 4자 이상 ~ 20자 이하의 ID를 입력해주세요.")
                _this.idInput.reportValidity();
                return;
            }
            let isDuplicated;
            try {
                isDuplicated = await _this.checkIdExist(userId);
            } catch (err) {
                return CustomEvent.error("중복 확인에 실패하였습니다. 동일한 오류가 계속 발생 시, 관리자에게 문의하여주세요.")
            }

            if (isDuplicated) {
                _this.idHelpMessage.innerText = "ⓘ 중복된 아이디입니다.";
                _this.idHelpMessage.classList.add("text-danger");
                _this.idInput.classList.remove("input-is-valid")
                _this.idInput.classList.add("input-is-invalid")
            } else {
                _this.hasUserIdUniqueCheck = true;
                _this.idHelpMessage.innerText = "";
                _this.idHelpMessage.classList.remove("text-danger");
                _this.idInput.classList.add("input-is-valid")
                _this.idInput.classList.remove("input-is-invalid")
            }
        })
    }

    checkPasswordEquals() {
        const password = this.passwordInput.value;
        const passwordConfirm = this.passwordCheckInput.value;

        if(passwordConfirm.length <= 0) {
            this.passwordHelpMessage2.classList.remove("text-danger");
            this.passwordHelpMessage2.classList.remove("text-primary");
            this.passwordHelpMessage2.innerText = "";
            this.passwordCheckInput.classList.add("input-is-invalid");
            this.passwordCheckInput.classList.remove("input-is-valid");
            this.isPasswordsEqual = false;
            return;
        }

        if(password === passwordConfirm) {
            this.passwordHelpMessage2.classList.remove("text-danger");
            this.passwordCheckInput.classList.remove("input-is-invalid");
            this.passwordHelpMessage2.innerText = "";
            this.passwordCheckInput.classList.add("input-is-valid");
            this.isPasswordsEqual = true;
        } else {
            this.passwordHelpMessage2.classList.add("text-danger");
            this.passwordHelpMessage2.innerText = "ⓘ 입력한 비밀번호가 일치하지 않습니다.";
            this.passwordCheckInput.classList.add("input-is-invalid");
            this.passwordCheckInput.classList.remove("input-is-valid");
            this.isPasswordsEqual = false;
        }
    }

    setEmailConfirmHelpText(yn) {
        if(yn === true) {
            this.emailConfirmHelpMessage.classList.remove("text-danger")
            this.emailConfirmHelpMessage.classList.add("text-primary");
            this.emailConfirmInput.classList.add("input-is-valid");
            this.emailConfirmInput.classList.remove("input-is-invalid");
            this.emailConfirmHelpMessage.innerText = "";
            this.hasEmailValidated = true;
        } else {
            this.emailConfirmHelpMessage.classList.remove("text-primary")
            this.emailConfirmHelpMessage.classList.add("text-danger")
            this.emailConfirmHelpMessage.innerText = "ⓘ 인증번호가 일치하지 않습니다.";
            this.emailConfirmInput.classList.add("input-is-invalid");
            this.emailConfirmInput.classList.remove("input-is-valid");
            this.hasEmailValidated = false;
        }
    }

    async checkIdExist(userId) {
        const url = "/user/findByUserId";
        const formData = new URLSearchParams($("#joinForm").serialize());
        
        return axios
        .post(url, formData)
        .then(function (response) {
            if(response.status != 200) {
                throw CustomAlert.error("Status not OK");
            }
            return response.data;
        })
        .catch(function (error) {
            return error.response.data;
        });
    }

    async sendEmailConfirmNumber() {
        /*if(!this.hasEmailUniqueCheck) {
            CustomAlert.error("이메일을 확인해 주세요.");
            return;
        }*/

        if(!this.hasUserIdUniqueCheck) {
            CustomAlert.error("아이디 중복확인을 먼저 진행해주세요.");
            return;
        }

        //인증번호 보내기
        const _this = this;
        /*const urlFormat = "/signup/send-confirm-email?username={0}&name={1}&email={2}"
        const url = urlFormat.format(_this.idInput.value, _this.nameInput.value, _this.emailInput.value);*/

        try {
            this.emailConfirmDiv.style.display = '';
            /*const response = await fetch(url);
            if(response.ok) {
                this.emailConfirmDiv.style.display = '';
            }else {
                const errorResponse = await response.json();
                alert(errorResponse.message);
                return false;
            }*/
        } catch (err) {
            return _this.returnHaveProblemAlert();
        }
    }

    returnHaveProblemAlert() {
        return CustomAlert.error("문제가 발생하였습니다. 동일한 오류 반복 시 관리자에게 문의해주세요.");
    }
}