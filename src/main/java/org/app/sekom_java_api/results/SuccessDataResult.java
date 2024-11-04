package org.app.sekom_java_api.results;

public class SuccessDataResult<T> extends DataResult<T> {
    public SuccessDataResult(T data, Result result){super(data,result);}
}
