package com.duowei.dw_pos.event;

import com.duowei.dw_pos.bean.WMLSB;
import com.duowei.dw_pos.bean.WMLSBJB;

import java.util.List;

/**
 * Created by Administrator on 2017-04-08.
 */

public class Commit {
    public boolean first=true;
    public WMLSBJB wmlsbjb;
    public List<WMLSB> wmlsbList;

    public Commit(boolean first,WMLSBJB wmlsbjb) {
        this.first=first;
        this.wmlsbjb = wmlsbjb;
    }

    public Commit(boolean first,WMLSBJB wmlsbjb, List<WMLSB> wmlsbList) {
        this.first=first;
        this.wmlsbjb = wmlsbjb;
        this.wmlsbList = wmlsbList;
    }
}
