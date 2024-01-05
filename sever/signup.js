router.post('/register', (req, res) =>{
  const body = req.body;
  const id = body.id;
  const pw = body.pw;

  connection.query('select * from user where id=?',[id],(err,data)=>{
    if(data.length == 0){
        console.log('회원가입 성공');
        connection.query('insert into user(id, password) values(?,?)',[id,pw]);
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