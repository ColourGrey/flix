namespace Test/Exp/Concurrency/Unbuffered {

    @test
    def testUnbufferedChannelPutGet01(): Bool \ IO = region r {
        let (tx, rx) = Channel.unbuffered(r);
        spawn Channel.send((), tx);
        () == Channel.recv(rx)
    }

    @test
    def testUnbufferedChannelPutGet02(): Bool \ IO = region r {
        let (tx, rx) = Channel.unbuffered(r);
        spawn Channel.send(true, tx);
        true == Channel.recv(rx)
    }

    @test
    def testUnbufferedChannelPutGet03(): Bool \ IO = region r {
        let (tx, rx) = Channel.unbuffered(r);
        spawn Channel.send(123.456f32, tx);
        123.456f32 == Channel.recv(rx)
    }

    @test
    def testUnbufferedChannelPutGet04(): Bool \ IO = region r {
        let (tx, rx) = Channel.unbuffered(r);
        spawn Channel.send(123.456f64, tx);
        123.456f64 == Channel.recv(rx)
    }

    @test
    def testUnbufferedChannelPutGet05(): Bool \ IO = region r {
        let (tx, rx) = Channel.unbuffered(r);
        spawn Channel.send(42i8, tx);
        42i8 == Channel.recv(rx)
    }

    @test
    def testUnbufferedChannelPutGet06(): Bool \ IO = region r {
        let (tx, rx) = Channel.unbuffered(r);
        spawn Channel.send(42i16, tx);
        42i16 == Channel.recv(rx)
    }

    @test
    def testUnbufferedChannelPutGet07(): Bool \ IO = region r {
        let (tx, rx) = Channel.unbuffered(r);
        spawn Channel.send(42i32, tx);
        42i32 == Channel.recv(rx)
    }
    
    @test
    def testUnbufferedChannelPutGet08(): Bool \ IO = region r {
        let (tx, rx) = Channel.unbuffered(r);
        spawn Channel.send(42i64, tx);
        42i64 == Channel.recv(rx)
    }

    @test
    def testUnbufferedChannelPutGet09(): Bool \ IO = region r {
        let (tx, rx) = Channel.unbuffered(r);
        spawn Channel.send(42ii, tx);
        42ii == Channel.recv(rx)
    }
    
    @test
    def testUnbufferedChannelPutGet10(): Bool \ IO = region r {
        let (tx, rx) = Channel.unbuffered(r);
        spawn Channel.send("Hello World!", tx);
        "Hello World!" == Channel.recv(rx)
    }
    
    @test
    def testUnbufferedChannelPutGet11(): Bool \ IO = region r {
        let (tx, rx): (Sender[Option[Int32], _], Receiver[Option[Int32], _]) = Channel.unbuffered(r);
        spawn Channel.send(None, tx);
        None == Channel.recv(rx)
    }
    
    @test
    def testUnbufferedChannelPutGet12(): Bool \ IO = region r {
        let (tx, rx) = Channel.unbuffered(r);
        spawn Channel.send(Some(123), tx);
        Some(123) == Channel.recv(rx)
    }
    
    @test
    def testUnbufferedChannelPutGet13(): Bool \ IO = region r {
        let (tx, rx): (Sender[Result[Int32, String], _], Receiver[Result[Int32, String], _]) = Channel.unbuffered(r);
        spawn Channel.send(Ok(123), tx);
        Ok(123) == Channel.recv(rx)
    }
    
    @test
    def testUnbufferedChannelPutGet14(): Bool \ IO = region r {
        let (tx, rx): (Sender[Result[Int32, String], _], Receiver[Result[Int32, String], _]) = Channel.unbuffered(r);
        spawn Channel.send(Err("Goodbye World!"), tx);
        Err("Goodbye World!") == Channel.recv(rx)
    }
    
    @test
    def testUnbufferedChannelPutGet15(): Bool \ IO = region r {
        let (tx, rx) = Channel.unbuffered(r);
        spawn Channel.unsafeSend([1, 2, 3], tx);
        2 == (Channel.recv(rx))[1]
    }
    
    @test
    def testUnbufferedChannelPutGet16(): Bool \ IO = region r {
        let (tx1, rx1) = Channel.unbuffered(r);
        let (tx2, rx2) = Channel.unbuffered(r);
        spawn Channel.unsafeSend(rx2, tx1);
        spawn Channel.send(42, tx2);
        42 == Channel.recv(Channel.recv(rx1))
    }
    
    @test
    def testUnbufferedChannelGetPut01(): Unit \ IO = region r {
        let (tx, rx) = Channel.unbuffered(r);
        spawn Channel.recv(rx);
        Channel.send((), tx)
    }
    
    @test
    def testUnbufferedChannelGetPut02(): Unit \ IO = region r {
        let (tx, rx) = Channel.unbuffered(r);
        spawn Channel.recv(rx);
        Channel.send(true, tx)
    }
    
    @test
    def testUnbufferedChannelGetPut03(): Unit \ IO = region r {
        let (tx, rx) = Channel.unbuffered(r);
        spawn Channel.recv(rx);
        Channel.send(123.456f32, tx)
    }
    
    @test
    def testUnbufferedChannelGetPut04(): Unit \ IO = region r {
        let (tx, rx) = Channel.unbuffered(r);
        spawn Channel.recv(rx);
        Channel.send(123.456f64, tx)
    }
    
    @test
    def testUnbufferedChannelGetPut05(): Unit \ IO = region r {
        let (tx, rx) = Channel.unbuffered(r);
        spawn Channel.recv(rx);
        Channel.send(42i8, tx)
    }
    
    @test
    def testUnbufferedChannelGetPut06(): Unit \ IO = region r {
        let (tx, rx) = Channel.unbuffered(r);
        spawn Channel.recv(rx);
        Channel.send(42i16, tx)
    }
    
    @test
    def testUnbufferedChannelGetPut07(): Unit \ IO = region r {
        let (tx, rx) = Channel.unbuffered(r);
        spawn Channel.recv(rx);
        Channel.send(42i32, tx)
    }
    
    @test
    def testUnbufferedChannelGetPut08(): Unit \ IO = region r {
        let (tx, rx) = Channel.unbuffered(r);
        spawn Channel.recv(rx);
        Channel.send(42i64, tx)
    }
    
    @test
    def testUnbufferedChannelGetPut09(): Unit \ IO = region r {
        let (tx, rx) = Channel.unbuffered(r);
        spawn Channel.recv(rx);
        Channel.send(42ii, tx)
    }
    
    @test
    def testUnbufferedChannelGetPut10(): Unit \ IO = region r {
        let (tx, rx) = Channel.unbuffered(r);
        spawn Channel.recv(rx);
        Channel.send("Hello World!", tx)
    }
    
    @test
    def testUnbufferedChannelGetPut11(): Unit \ IO = region r {
        let (tx, rx): (Sender[Option[Int32], _], Receiver[Option[Int32], _]) = Channel.unbuffered(r);
        spawn Channel.recv(rx);
        Channel.send(None, tx)
    }
    
    @test
    def testUnbufferedChannelGetPut12(): Unit \ IO = region r {
        let (tx, rx) = Channel.unbuffered(r);
        spawn Channel.recv(rx);
        Channel.send(Some(123), tx)
    }
    
    @test
    def testUnbufferedChannelGetPut13(): Unit \ IO = region r {
        let (tx, rx): (Sender[Result[Int32, String], _], Receiver[Result[Int32, String], _]) = Channel.unbuffered(r);
        spawn Channel.recv(rx);
        Channel.send(Ok(123), tx)
    }
    
    @test
    def testUnbufferedChannelGetPut14(): Unit \ IO = region r {
        let (tx, rx): (Sender[Result[Int32, String], _], Receiver[Result[Int32, String], _]) = Channel.unbuffered(r);
        spawn Channel.recv(rx);
        Channel.send(Err("Goodbye World!"), tx)
    }
    
    @test
    def testUnbufferedChannelGetPut15(): Unit \ IO = region r {
        let (tx, rx) = Channel.unbuffered(r);
        spawn Channel.recv(rx);
        Channel.unsafeSend([1, 2, 3], tx)
    }
    
    @test
    def testUnbufferedChannelGetPut16(): Unit \ IO = region r {
        let (tx1, rx1) = Channel.unbuffered(r);
        let (tx2, rx2) = Channel.unbuffered(r);
        spawn Channel.recv(Channel.recv(rx1));
        spawn Channel.unsafeSend(rx2, tx1);
        Channel.send(42, tx2)
    }
}
