## 개발 스택

- 언어: kotlin
- 서버: nodejs
- DB: 유저 정보를 저장을 위해 mysql 사용, 채팅 기록 저장을 위해 mongodb 사용

## 어플리케이션 소개

- 친구 추천 서비스 (Finder)
    - 사는 지역, mbti, 취미 등 여러 기준을 주어, 그에 맞는 유저를 추천해주는 서비스
    - 회원가입을 통해 유저 정보들을 받아 이를 기준으로 유저를 추천
    - 카드 형태로 유저 프로필을 제시
    - 더 나아가 추천 받은 유저와 채팅도 할 수 있는 기능 구현

## 초기 화면

- 아이디와 비밀번호를 입력하면 로그인할 수 있음.
- REGISTER를 클릭하면 회원가입 창으로 넘어가 회원가입을 할 수 있음.
- 구글 아이디를 통해서도 로그인 할 수 있음
- 기술 설명
    - 로그인 구현을 위해서는 데이터베이스에서 아이디와 비밀번호가 모두 일치하는 tuple이 있으면 로그인 성공과 함께 메인 화면으로 넘어가도록 함.
    - 구글 api 로그인 구현

## 회원가입 화면

- 프로필 사진, 아이디, 비밀번호, mbti, 취미 등의 정보를 입력할 수 있게 하고 지역 추가를 눌렀을 때 지도로 넘어가 현재 위치가 뜨고 오른쪽 하단에 ok 버튼을 누르면 자동으로 현재 위치의 시, 구에 관한 정보가 저장됨.
- REGISTER 버튼을 누르면 회원가입이 완료되고 데이터베이스에 유저 정보가 저장됨.
- 기술 설명
    - REGISTER 버튼을 눌렀을 때 아이디, 비밀번호, mbti, 취미, 사는 지역 등의 정보가 insert into 쿼리문을 통해 데이터베이스에 추가되도록 함.
    - 아이디가 중복되면 같은 아이디가 있다는 메시지와 함께 회원가입이 되지 않도록 구현함.
    - 사는 지역에 대한 정보를 얻기 위해 google map api를 사용하였고 현재 위치의 위도와 경도를 바탕으로 OO시 OO구의 형태로 변환하기 위해 geocoer를 사용함.

## 메인 화면

- 상단에 앱 로고와 함께 ‘$(userid)님 안녕하세요!’라는 텍스트가 뜨고 내 정보 보기를 통해 내 정보를 확인할 수 있음
- 내 정보 보기를 누르면 사용자의 프로필 사진, 아이디, mbti, 취미, 사는 지역 등의 정보가 작은 화면에 뜨고 ‘내 정보 수정’과 ‘확인’ 버튼이 있음.
- 내 정보 수정을 누르면 새로운 창으로 넘어가 비밀번호, mbti, 취미, 사는 지역 등을 변경할 수 있음.
- 하단에 mbti 선택, 취미 선택 등을 고를 수 있는 picker가 있고 picker에서 정보를 선택한 후 친구 매칭을 누르면 나와 같은 지역에 사는 유저와 함께 선택한 mbti, 취미 등을 가진 유저가 카드뷰 형태로 뜸.
- 카드뷰에서 추천에 뜬 유저의 아이디, mbti, 취미, 사는 지역 등을 확인할 수 있고 옆으로 스크롤을 하여 여러 유저들을 확인할 수 있음. 카드뷰 오른쪽 하단의 채팅하기 버튼을 누르면 해당 유저와의 채팅방으로 넘어가 채팅을 할 수 있음.
- 기술 설명
    - jwt 토큰을 발행하여 다른 창으로 넘어갈 때마다 토큰을 함께 넘겨주어 user가 본인 토큰을 가지고 있도록 하고 이 토큰을 바탕으로 내 정보 보기를 구현함.
    - 내 정보 수정은 회원가입과 비슷한 코드로 구현하였고 서버의 query문의 내용을 update로 바꾸어 구현함.

## 채팅 화면

- 상단에 상대방의 아이디가 뜨고 하단에 내용을 입력할 수 있는 edittext와 내용을 전송할 때 쓰는 버튼이 있음
- 내가 보낸 메시지는 내용과 함께 아래에 작은 글씨로 보낸 날짜와 시간이 뜨고 상대방이 보낸 메시지는 상대방의 프로필 사진, 아이디와 함께 보낸 내용과 날짜와 시간이 뜸.
- 채팅창
    - 채팅창 탭에서는 나와 채팅한 사람들과의 채팅방이 시간 순서대로 뜸.
    - 채팅방의 이름을 통해 채팅방을 검색할 수 있고 특정 채팅방을 누르면 해당 채팅방으로 넘어가 채팅을 할 수 있음.
- 기술 설명
    - 소켓 통신을 통해 실시간 채팅 기능을 구현함.
    - 채팅을 보냈을 때 자동으로 mongodb에 저장되도록 하여 채팅 기록을 저장함.
