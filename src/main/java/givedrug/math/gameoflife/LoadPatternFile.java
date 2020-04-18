package givedrug.math.gameoflife;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LoadPatternFile {

    public boolean[][] getPatternMap(String fileName,int boundx,int boundy) {
        boolean[][] map=new boolean[boundy][boundx];

        try (InputStream inputStream = getClass().getResourceAsStream("/"+fileName)) {
            if (inputStream != null) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String str = null;
                int count = 0;
                while((str = bufferedReader.readLine()) != null){
                    System.out.println(str);
                    if(str.startsWith(".")||str.startsWith("O")){
                        for(int i=0;i<str.length();i++){
                            char c = str.charAt(i);
                            map[count][i] = c=='.'?false:true;
                        }
                        count++;
                    }
                }
                inputStream.close();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

}
