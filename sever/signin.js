router.post('/login', (req, res)=>{
  const body = req.body;
  const id = body.id;
  const pw = body.pw;

  connection.query('select id, password from user where id=? and password=?', [id,pw], (err, data)=>{
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
      connection.query('select UID from user where id=?',[id],(err,data)=>{
        res.status(200).send(data[0]);
      });

    }
  });

});