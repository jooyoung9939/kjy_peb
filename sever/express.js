const express = require('express');
const cors = require('cors');
const app = express();
const port = 8000;
const corsOptions = {
  origin: '*', // 모든 도메인에 대해 접근 허용
  methods: 'GET,HEAD,PUT,PATCH,POST,DELETE',
  credentials: true,
  optionsSuccessStatus: 204
};

// 라우팅 및 미들웨어 등을 여기에 추가
app.use(cors(corsOptions));

app.get('/', (req, res) => {
  res.send('Hello World!');
});

app.listen(port, () => {
  console.log(`서버가 https://localhost:${port} 에서 실행 중입니다.`);
});

app.get("/members", function (req, res) {
  var sql = "select * from user";
  con.query(sql, function (err, results, fields) {
    if (err) {
      res.status(500).json({ error: "Internal Server Error" });
    } else {
      res.json(results);
    }
  });
});
