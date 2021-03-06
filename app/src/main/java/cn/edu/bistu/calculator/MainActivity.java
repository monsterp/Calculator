package cn.edu.bistu.calculator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
//     Button button0;
//     Button button1;
//     Button button2;
//     Button button3;
//     Button button4;
//     Button button5;
//     Button button6;
//     Button button7;
//     Button button8;
//     Button button9;
//     Button buttonAC;
//     Button buttonJia;
//     Button buttonJian;
//     Button buttonCheng;
//     Button buttonChu;
//     Button buttonDel;
//     Button buttonDeng;
//     Button buttonDian;
//     EditText view;


    private Button[] buttons = new Button[18];
    private int[] ids = new int[]{R.id.button0,R.id.button1,R.id.button2,R.id.button3,R.id.button4,R.id.button5,R.id.button6,
            R.id.button7,R.id.button8,R.id.button9,R.id.buttonAC,R.id.buttonJia,R.id.buttonJian,R.id.buttonCheng,R.id.buttonChu,R.id.buttonDian,R.id.buttonDel,R.id.buttonDeng
    };
    private TextView textView;
    private String viwe = "";
    private boolean end = false;
    private int countOperate=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        for(int i=0;i<ids.length;i++){
            buttons[i] = (Button)findViewById(ids[i]);
            buttons[i].setOnClickListener(this);
        }
        textView = (TextView)findViewById(R.id.textView);
    }
    public void onClick(View view) {
        int id = view.getId();
        Button button = (Button)view.findViewById(id);
        String current = button.getText().toString();
        if(end){
            viwe = "";
            end = false;
        }
        if(current.equals("AC")){
            viwe = "";
            countOperate=0;
        }else if(current.equals("Del")){
            if(viwe.length()>1){ //算式长度大于1
                viwe = viwe.substring(0,viwe.length()-1);//退一格
                int i = viwe.length()-1;
                char tmp = viwe.charAt(i); //获得最后一个字符
                char tmpFront = tmp;
                for(;i>=0;i--){ //向前搜索最近的 +-*/和.，并退出
                    tmpFront = viwe.charAt(i);
                    if(tmpFront=='.'||tmpFront=='+'||tmpFront=='-'||tmpFront=='*'||tmpFront=='/'){
                        break;
                    }
                }
                if(tmp>='0'&&tmp<='9'){ //最后一个字符为数字，则识别数赋值为0
                    countOperate=0;
                }else if(tmp==tmpFront&&tmpFront!='.') countOperate=2; //如果为+-*/，赋值为2
                else if(tmpFront=='.') countOperate=1; //如果前面有小数点赋值为1
            }else if(viwe.length()==1){
                viwe = "";
            }
        }else if(current.equals(".")){
            if(viwe.equals("")||countOperate==2){
                viwe+="0"+current;
                countOperate = 1;  //小数点按过之后赋值为1
            }
            if(countOperate==0){
                viwe+=".";
                countOperate = 1;
            }
        }else if(current.equals("+")||current.equals("-")||current.equals("*")||current.equals("/")){
            if(countOperate==0){
                viwe+=current;
                countOperate = 2;  //  +-*/按过之后赋值为2
            }
        }else if(current.equals("=")){ //按下=时，计算结果并显示
            double result = (double) Math.round(count()*100)/100;
            viwe+="="+result;
            end = true; //此次计算结束
        }
        else{//此处是当退格出现2+0时，用current的值替代0
            if(viwe.length()>=1){
                char tmp1 = viwe.charAt(viwe.length()-1);
                if(tmp1=='0'&&viwe.length()==1){
                    viwe = viwe.substring(0,viwe.length()-1);
                }
                else if(tmp1=='0'&&viwe.length()>1){
                    char tmp2 = viwe.charAt(viwe.length()-2);
                    if(tmp2=='+'||tmp2=='-'||tmp2=='*'||tmp2=='/'){
                        viwe = viwe.substring(0,viwe.length()-1);
                    }
                }
            }
            viwe+=current;
            if(countOperate==2||countOperate==1) countOperate=0;
        }
        textView.setText(viwe); //显示出来
    }
    private double count(){
        double result=0;
        double tNum=1,lowNum=0.1,num=0;
        char tmp=0;
        int operate = 1; //识别+-*/，为+时为正数，为-时为负数，为×时为-2/2,为/时为3/-3;
        boolean point = false;
        for(int i=0;i<viwe.length();i++){ //遍历表达式
            tmp = viwe.charAt(i);
            if(tmp=='.'){ //因为可能出现小数，此处进行判断是否有小数出现
                point = true;
                lowNum = 0.1;
            }else if(tmp=='+'||tmp=='-'){
                if(operate!=3&&operate!=-3){ //此处判断通用，适用于+-*
                    tNum *= num;
                }else{ //计算/
                    tNum /= num;
                }
                if(operate<0){ //累加入最终的结果
                    result -= tNum;
                }else{
                    result += tNum;
                }
                operate = tmp=='+'?1:-1;
                num = 0;
                tNum = 1;
                point = false;
            }else if(tmp=='×'){
                if(operate!=3&&operate!=-3){
                    tNum *= num;
                }else{
                    tNum /= num;
                }
                operate = operate<0?-2:2;
                point = false;
                num = 0;
            }else if(tmp=='÷'){
                if(operate!=3&&operate!=-3){
                    tNum *= num;
                }else{
                    tNum /= num;
                }
                operate = operate<0?-3:3;
                point = false;
                num = 0;
            }else{
                //读取expression中的每个数字，doube型
                if(!point){
                    num = num*10+tmp-'0';
                }else{
                    num += (tmp-'0')*lowNum;
                    lowNum*=0.1;
                }
            }
        }
        //循环遍历结束，计算最后一个运算符后面的数
        if(operate!=3&&operate!=-3){
            tNum *= num;
        }else{
            tNum /= num;
        }
        if(operate<0){
            result -= tNum;
        }else{
            result += tNum;
        }
        //返回最后的结果
        return result;
    }
}
