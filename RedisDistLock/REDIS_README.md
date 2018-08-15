

http://127.0.0.1:9999/swagger-ui.html


    public boolean lock(String key,String value){
        if(stringRedisTemplate.opsForValue().setIfAbsent(key,value)){//对应setnx命令
            //可以成功设置,也就是key不存在
            return true;
        }

        //判断锁超时 - 防止原来的操作异常，没有运行解锁操作  防止死锁
        String currentValue = stringRedisTemplate.opsForValue().get(key);
        //如果锁过期
        if(!StringUtils.isEmpty(currentValue) && Long.parseLong(currentValue) < System.currentTimeMillis()){//currentValue不为空且小于当前时间
            //获取上一个锁的时间value
            String oldValue =stringRedisTemplate.opsForValue().getAndSet(key,value);//对应getset，如果key存在

            //假设两个线程同时进来这里，因为key被占用了，而且锁过期了。获取的值currentValue=A(get取的旧的值肯定是一样的),两个线程的value都是B,key都是K.锁时间已经过期了。
            //而这里面的getAndSet一次只会一个执行，也就是一个执行之后，上一个的value已经变成了B。只有一个线程获取的上一个值会是A，另一个线程拿到的值是B。
            if(!StringUtils.isEmpty(oldValue) && oldValue.equals(currentValue) ){
                //oldValue不为空且oldValue等于currentValue，也就是校验是不是上个对应的商品时间戳，也是防止并发
                return true;
            }
        }
        return false;
    }
    
    
    只能自己实现RedisCallBack底层，采用RedisTemplate的SesionCallback来完成在同一个Connection中，完成多个操作的方法：
    
                 
    
        SessionCallback<Object>   sessionCallback=new SessionCallback<Object>(){
          @Override
           public Object execute(RedisOperations operations) throws DataAccessException{
        operations.multi();
        operations.delete("test");
        operations.opsForValue.set("test","2");
        Object val=operations.exec();
        return val;
        }
        }
        StringRedisTemplate.execute(sessionCallback);