const express = require('express');
const bodyParser = require('body-parser');
const mysql = require('mysql2');
const cors = require('cors');

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

app.use(bodyParser.urlencoded({extended: true }));
app.use(bodyParser.json());

app.get('/', (req, res) => {
    res.send('Hello World!');
  });

app.post('/register', (req, res) =>{
    const body = req.body;
    const id = body.id;
    const pw = body.pw;
  
    connection.query('select * from users where users_id=?',[id],(err,data)=>{
      if(data.length == 0){
          console.log('회원가입 성공');
          connection.query('insert into users(users_id, users_pw) values(?,?)',[id,pw]);
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
        connection.query('select UID from users where users_id=?',[id],(err,data)=>{
          res.status(200).send(data[0]);
        });
  
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

app.listen(port, () => {
  console.log(`Server is running on port ${port}`);
});


