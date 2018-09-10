package com.example.quartz.common.util;

import com.example.quartz.common.rest.PageData;
import com.example.quartz.common.rest.RestMessage;
import org.springframework.data.domain.Page;

public class RestMessageUtil {

    /**
     *
     * @param page
     * @return
     */
    public static RestMessage pageToRestMessage(Page page){

        RestMessage restMessage=new RestMessage();

        PageData pd= new PageData();
        pd.setPageNum(page.getNumber()+1);
        pd.setPages(page.getTotalPages());
        pd.setPageSize(page.getSize());
        pd.setTotal(page.getTotalElements());
        pd.setList(page.getContent());

        restMessage.setData(pd);

        return restMessage;

    }

    /**
     *
     * @param object
     * @return
     */
    public static RestMessage objectToRestMessage(Object object){

        RestMessage restMessage=new RestMessage();
        restMessage.setData(object);

        return restMessage;

    }
}
