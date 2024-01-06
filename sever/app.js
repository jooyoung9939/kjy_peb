const express = require('express');
const bodyParser = require('body-parser');
const mysql = require('mysql2');
const cors = require('cors');
const jwt = require('jsonwebtoken');

const app = express();
const port = 3000;

const corsOptions = {
    origin: '*', // 모든 도메인에 대해 접근 허용
    methods: 'GET,HEAD,PUT,PATCH,POST,DELETE',
    credentials: true,
    optionsSuccessStatus: 204
  };
  
  // 라우팅 및 미들웨어 등을 여기에 추가
  app.use(cors(corsOptions));

const connection = mysql.createConnection({
  host: 'localhost',
  user: 'root',
  password: '*ilove0309',
  database: 'madcamp_week2'
});

connection.connect((err) => {
  if (err) {
    console.error('MySQL connection error:', err);
  } else {
    console.log('Connected to MySQL database');
  }
});

app.use(express.json());

app.use(bodyParser.urlencoded({extended: true }));
app.use(bodyParser.json());

const secretKey = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Imdlb3JnZTAzMDkiLCJpYXQiOjE3MDQ1MzE0NzAsImV4cCI6MTcwNDUzNTA3MH0.d32w6gmjba0j2Mzm-8GMCl2H2QHEENMghhMPSqzcGOA'; // 안전한 방식으로 보관하세요

// 토큰 검증 미들웨어
function authenticateToken(req, res, next) {
  const token = req.header('Authorization');
  if (!token) {
  console.error('No token provided.');
  return res.status(401).json({ message: 'Access denied. Token not provided.' });
  }

  jwt.verify(token, secretKey, (err, user) => {
      if (err) {
        console.error('Token verification failed:', err);
        return res.status(403).json({ message: 'Invalid token.' });
    }

      req.user = user;
      next();
  });
}

app.get('/', (req, res) => {
    res.send('Hello World!');
  });

  app.post('/register', (req, res) =>{
    const body = req.body;
    const id = body.id;
    const pw = body.pw;
    const image = body.image;
  
    connection.query('select * from users where users_id=?',[id],(err,data)=>{
      if(data.length == 0){
          console.log('회원가입 성공');
          connection.query('insert into users(users_id, users_pw, image) values(?,?,?)',[id,pw,image]);
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

  app.get('/my_info', authenticateToken, (req, res) => {
    console.log('Authorization Header:', req.header('Authorization'));
    // 여기에 토큰이 유효할 때 수행할 동작을 추가
    const userId = req.user.id;

    // 데이터베이스에서 사용자 정보 조회 (가정: users 테이블이 있다고 가정)
    connection.query('SELECT * FROM users WHERE users_id = ?', [userId], (error, results) => {
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
              id: results[0].users_id,
              pw: results[0].users_pw
                // 기타 사용자 정보 필드들을 추가
            };
            res.json(userInfo);
        }
    });
});

app.listen(port, () => {
  console.log(`Server is running on port ${port}`);
});


