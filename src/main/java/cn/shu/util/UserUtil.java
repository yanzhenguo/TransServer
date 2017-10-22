package cn.shu.util;

import java.util.Date;
import java.util.Random;

public class UserUtil {

    //�����û���
    public static String generateUserName(String filmId){
        Date date = new Date();
        String dateTime = String.valueOf(date.getTime());
        return filmId+"_"+dateTime;
    }

    //�����������

    /**
     *
     * @param n ���볤��
     * @return
     */
    public static String generatePass(int n){
        Random random = new Random();
        int numLower = random.nextInt(n-2)+1;
        int numCapital = random.nextInt(n-numLower-1)+1;
        int numNum = n-numLower-numCapital;
        char[] str = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                'x', 'y', 'z', 'A','B','C','D','E','F','G','H','I','J','K',
                'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
                'X', 'Y' ,'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
        StringBuilder pass=new StringBuilder();
        while(numLower>0 || numCapital>0 || numNum>0){
            int i = random.nextInt(62);
            if(i>=0 && i<=25 && numLower>0) {
                pass.append(str[i]);
                numLower--;
            }
            else if(i>=26 && i<=51 && numCapital>0) {
                pass.append(str[i]);
                numCapital--;
            }
            else if(i>=52 && i<=61 && numNum >0) {
                pass.append(str[i]);
                numNum--;
            }
        }
        return pass.toString();
    }
}
