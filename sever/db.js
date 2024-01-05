const mysql = require('mysql2');

// MySQL 연결 정보
const connection = mysql.createConnection({
  host: 'localhost',
  user: 'root',
  password: '*ilove0309',
  database: 'madcamp_week2'
});

// MySQL 연결
connection.connect((err) => {
  if (err) {
    console.error('Error connecting to MySQL:', err);
    return;
  }
  console.log('Connected to MySQL database');
});

module.exports = connection;
