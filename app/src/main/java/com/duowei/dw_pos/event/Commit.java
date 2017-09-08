package com.duowei.dw_pos.event;

import com.duowei.dw_pos.bean.WMLSB;
import com.duowei.dw_pos.bean.WMLSBJB;

import java.util.List;

/**
 * Created by Administrator on 2017-04-08.
 */

public class Commit {
    public WMLSBJB wmlsbjb;
    public List<WMLSB> wmlsbList;
    public String seconds;

    public Commit(WMLSBJB wmlsbjb) {
        this.wmlsbjb = wmlsbjb;
    }

    public Commit(WMLSBJB wmlsbjb, List<WMLSB> wmlsbList) {
        this.wmlsbjb = wmlsbjb;
        this.wmlsbList = wmlsbList;
    }

    public Commit(WMLSBJB wmlsbjb, List<WMLSB> wmlsbList, String seconds) {
        this.wmlsbjb = wmlsbjb;
        this.wmlsbList = wmlsbList;
        this.seconds = seconds;
    }
}
