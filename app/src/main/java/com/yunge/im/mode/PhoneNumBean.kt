package com.yunge.im.mode

class PhoneNumBean {
    var phoneNum1: String? = null
    var phoneNum2: String? = null
    private var simIndex: Int = -1
    var currentSimIndex: Int? = null
    var callTwoSimMode:String = "";
    var isAutoCall:Boolean = true;
    var isHz:Boolean = true;
    var waitTime:Int = 4;
    var yys:Int = -1;
    var currentyysIndex: Int? = null

    public fun getSimIndex():Int {
        if (callTwoSimMode == "1" || callTwoSimMode == ""){
            return simIndex;
        }else{
            return simIndex +1;

        }

    }

    public fun setSimIndex(index:Int){
        simIndex = index;
    }
}