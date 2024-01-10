const express = require('express');
const bodyParser = require('body-parser');
const mysql = require('mysql2');
const cors = require('cors');
const jwt = require('jsonwebtoken');
const crypto = require('crypto');
const http = require('http');
const socketIO = require('socket.io');
const mongoose = require('mongoose');
const path = require('path');

const app = express();
const port = process.env.PORT || 3000;

const corsOptions = {
    origin: '*', // 모든 도메인에 대해 접근 허용
    methods: 'GET,HEAD,PUT,PATCH,POST,DELETE',
    credentials: true,
    optionsSuccessStatus: 204
  };
  
  // 라우팅 및 미들웨어 등을 여기에 추가
  app.use(cors(corsOptions));

const connection = mysql.createPool({
  host: 'localhost',
  user: 'root',
  password: '*ilove0309',
  database: 'madcamp_week2',
  connectionLimit: 2,
  waitForConnections: true,
  queueLimit: 0,
  keepAliveInitialDelay: 10000,
  enableKeepAlive: true
});

// connection.connect((err) => {
//   if (err) {
//     console.error('MySQL connection error:', err);
//   } else {
//     console.log('Connected to MySQL database');
//   }
// });

app.use(express.json());

app.use(bodyParser.urlencoded({extended: true }));
app.use(bodyParser.json());

function generateRandomKey(length) {
  return crypto.randomBytes(length).toString('hex');
}

// 예시: 길이가 32인 랜덤 시크릿 키 생성
const secretKey = generateRandomKey(32);
console.log('Random Secret Key:', secretKey);

const { OAuth2Client } = require('google-auth-library');
const CLIENT_ID = '648978352693-anm2jkrauoa94q7vp7h338mdcm97vp7s.apps.googleusercontent.com';
const client2 = new OAuth2Client(CLIENT_ID);


// 토큰 검증 미들웨어
function authenticateToken(req, res, next) {
  const authHeader = req.header('Authorization');
  console.log(authHeader);

  if (!authHeader) {
    console.error('No token provided.');
    return res.status(401).json({ message: 'Access denied. Token not provided.' });
  }

  const token = authHeader.split(' ')[1]; // "Bearer " 제거
  console.log(token);

  jwt.verify(token, secretKey, (err, user) => {
    if (err) {
      const decoded = jwt.decode(token);
      console.log('Decoded token:', decoded);
      console.error('Token verification failed:', err);
      return res.status(403).json({ message: 'Invalid token.' });
    } else {
      const decoded = jwt.decode(token);
      console.log('Decoded token:', decoded);

      req.user = user;
      next();
    }
  });
}

app.get('/', (req, res) => {
    res.send('Hello World!');
  });

  app.post('/register', (req, res) =>{
    const body = req.body;
    const id = body.id;
    const pw = body.pw;
    const mbti = body.mbti;
    const hobby = body.hobby;
    const region = body.region;
    // const image = body.image;

    connection.query('select * from users where users_id=?',[id],(err,data)=>{
      if(data.length == 0){
          console.log('회원가입 성공');
          connection.query('insert into users(users_id, users_pw, users_mbti_id, users_hobby_id, users_region) values(?,?,?,?,?)',[id,pw,mbti,hobby,region]);
          res.status(200).json(
            {
              "message" : true
            }
          );
      }else{
          console.log('회원가입 실패');
          res.status(200).json(
            {
              "message" : false
            }
          );
  
      }
  
    });
  });

app.post('/login', (req, res)=>{
    const body = req.body;
    const id = body.id;
    const pw = body.pw;
  
    connection.query('select users_id, users_pw from users where users_id=? and users_pw=?', [id,pw], (err, data)=>{
      if(data.length == 0){ // 로그인 실패
        console.log('로그인 실패');
        res.status(200).json(
          {
            "UID" : -1
          }
        )
      }
      else{
        // 로그인 성공
        console.log('로그인 성공');
        const user = {
          id: id,
          UID: data[0].UID
        };
        const accessToken = jwt.sign(user, secretKey, { expiresIn: '1h' });
  
        connection.query('select UID from users where users_id=?',[id],(err,data)=>{
          res.status(200).json({
            accessToken: accessToken,
            UID: data[0].UID
          });
        });
        console.log(accessToken)

        const decoded = jwt.decode(accessToken);
        console.log('Decoded Token:', decoded);
      }
    });
  
  });

app.get('/users_info', (req, res) => {

    connection.query('SELECT * FROM users', (error, rows) => {
      if(error) throw error;
      console.log('user info is : ', rows);
  
      res.status(200).send(rows)
  
    });
  });

  app.get('/matched_users_info', (req, res) => {
    const mbti = req.query.mbti;
    const hobby = req.query.hobby;
    const region = req.query.region;

    // 수정된 쿼리문
    const query = `
        SELECT
            u.users_id,
            m.mbtis_name AS users_mbti,
            h.hobbies_name AS users_hobby,
            u.users_region
        FROM
            users u
            LEFT JOIN mbtis m ON u.users_mbti_id = m.MID
            LEFT JOIN hobbies h ON u.users_hobby_id = h.HID
        WHERE
            u.users_mbti_id = ? or u.users_hobby_id = ? or u.users_region = ?
    `;

    connection.query(query, [mbti, hobby, region], (error, rows) => {
        if (error) {
            console.error('Error fetching matched users information:', error);
            res.status(500).json({ error: 'Internal Server Error' });
            return;
        }

        console.log('Matched user info is:', rows);
        res.status(200).send(rows);
    });
});

app.post('/edit_my_info', authenticateToken, (req, res) => {
  const userId = req.user.id;
  const { newPassword, newMbti, newHobby, newRegion } = req.body;

  // 여기서는 단순히 성공적으로 업데이트되었다고 가정합니다.
  // 실제로는 DB 업데이트 로직을 추가해야 합니다.
  // UPDATE 쿼리를 사용하여 사용자 정보를 업데이트하는 로직을 추가해야 합니다.
  // 여기서는 가정상의 코드입니다.

  connection.query('UPDATE users SET users_pw=?, users_mbti_id=?, users_hobby_id=?, users_region=? WHERE users_id=?', [newPassword, newMbti, newHobby, newRegion, userId], (err, data) => {
      if (!err) {
          console.log('사용자 정보 업데이트 성공');
          res.status(200).json({
              message: true
          });
      } else {
          console.error('사용자 정보 업데이트 실패', err);
          res.status(500).json({
              message: false
          });
      }
  });
});



  app.get('/my_info', authenticateToken, (req, res) => {
    console.log('Authorization Header:', req.header('Authorization'));
    // 여기에 토큰이 유효할 때 수행할 동작을 추가
    const userId = req.user.id;

    // 데이터베이스에서 사용자 정보 조회 (가정: users 테이블이 있다고 가정)
    const query = `
        SELECT
            u.UID,
            u.users_id,
            u.users_pw,
            m.mbtis_name AS users_mbti,
            h.hobbies_name AS users_hobby,
            u.users_region
        FROM
            users u
            LEFT JOIN mbtis m ON u.users_mbti_id = m.MID
            LEFT JOIN hobbies h ON u.users_hobby_id = h.HID
        WHERE
            u.users_id = ?
    `;

    connection.query(query, [userId], (error, results) => {

        if (error) {
            console.error('Error fetching user information:', error);
            res.status(500).json({ error: 'Internal Server Error' });
            return;
        }

        if (results.length === 0) {
            // 사용자 정보가 없을 경우
            res.status(404).json({ error: 'User not found' });
        } else {
            // 사용자 정보를 클라이언트에게 반환
            const userInfo = {
              UID: results[0].UID,
              users_id: results[0].users_id,
              users_pw: results[0].users_pw,
              users_mbti: results[0].users_mbti,
              users_hobby: results[0].users_hobby,
              users_region: results[0].users_region
                // 기타 사용자 정보 필드들을 추가
            };
            console.log(userInfo);
            res.json(userInfo);
        }
    });
});

// ...

app.post('/google_login', (req, res) => {
  const { idToken } = req.body;

  console.log(idToken)

  // TODO: 서버에서 idToken 검증 및 사용자 정보 확인 로직 추가
  // 여기에 서버에서 idToken을 검증하고 사용자 정보를 확인하는 코드를 작성하면 됩니다.
  // 예시: 토큰 검증이 성공하면 유저 정보를 응답으로 보냄

  // Verify Google ID Token
  clien2.verifyIdToken({
      idToken: idToken,
      audience: CLIENT_ID
  }).then(ticket => {
      const payload = ticket.getPayload();
      const userId = payload['sub'];
      const userInfo = {
          id: userId,
          // 여기에 사용자 정보 추가
      };
      res.status(200).json({ message: 'Google login successful', userInfo: userInfo });
  }).catch(error => {
      console.error('Google login failed:', error);
      res.status(401).json({ message: 'Google login failed' });
  });
});


const server = http.createServer(app);
const io = socketIO(server);
console.log("outside io");

// MongoDB에 연결
mongoose.connect('mongodb://localhost:27017/chatDB', {
  useNewUrlParser: true,
  useUnifiedTopology: true,
});

// 연결된지 확인하는 이벤트 리스너
mongoose.connection.once('open', () => {
  console.log('Connected to MongoDB');
});

// 채팅 모델 정의
const Chat = mongoose.model('Chat', {
  name: String,
  script: String,
  profile_image: String,
  date_time: String,
  roomName: String,
});

io.on('connection',  function(socket) {
    // 소켓 연결 이벤트 처리
    console.log('User Connection');

    socket.on('connect user', function (user) {
        console.log("Connected user ");
        socket.join(user['roomName']);
        console.log("roomName : ", user['roomName']);
        console.log("state : ", socket.adapter.rooms);
        io.emit('connect user', user);
    });

    socket.on('chat message', async function (msg) {
        console.log("Message " + msg['message']);
        console.log("보내는 아이디 : ", msg['rommName']);

            // MongoDB에 채팅 내용 저장
    const chat = new Chat({
      name: msg['name'],
      script: msg['script'],
      profile_image: msg['profile_image'],
      date_time: msg['date_time'],
      roomName: msg['roomName'],
    });

    await chat.save();

        io.to(msg['roomName']).emit('chat message', msg);
    });
});

app.get('/chats', authenticateToken, async (req, res) => {
  const userId = req.user.id;
  console.log(userId)

  try {
    const chatsCursor = Chat.find({ roomName: { $regex: userId } }).sort({ date_time: -1 });
    const chats = await chatsCursor.exec();
    res.json(chats);
  } catch (error) {
    console.error(error);
    res.status(500).json({ error: 'Internal Server Error' });
  }
}); 

server.listen(port, () => {
    console.log(`Server is running on port ${port}`);
});

