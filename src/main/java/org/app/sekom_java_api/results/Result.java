package org.app.sekom_java_api.results;

public class Result {

    protected final int resultCode;
    protected final String resultText;
    public ResultMessage resultMessage;

    public static final Result SUCCESS = new Result(0,"OK!");
    public static final Result SERVER_ERROR = new Result(1,"SERVER ERROR!");
    public static final Result SUCCESS_EMPTY = new Result(3, "OK BUT EMPTY!");
    public Result(Result result) {
        this.resultCode = result.resultCode;
        this.resultText = result.resultText;
        this.resultMessage = result.resultMessage;
    }

    public Result(int resultCode, String resultText) {
        this.resultCode = resultCode;
        this.resultText = resultText;
    }

    public static Result showMessage(Result resultType, ResultMessageType resultMessageType, String messageText) {
        Result result = new Result(resultType);
        result.resultMessage = new ResultMessage(resultMessageType,messageText);
        return result;
    }
}
