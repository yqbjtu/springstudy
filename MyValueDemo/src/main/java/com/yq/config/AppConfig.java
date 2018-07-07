package com.yq.config;
import com.yq.domain.Bonus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource("classpath:/appConf.xml")
public class AppConfig  {
    private @Value("#{entProp['bonus.name']}") String bonusName;
    private @Value("#{entProp['bonus.time']}") long bonusTime;
    private @Value("#{entProp['bonus.count']}") int bonusCount;

    @Bean(name="bonus")
    public Bonus bonus(){
        Bonus bonus = new Bonus();
        bonus.setName(bonusName);
        bonus.setTime(bonusTime);
        bonus.setCount(bonusCount);
        return bonus;
    }

}