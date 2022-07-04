package com.example.minio.constant;

/**
 * @author 李奇凇
 * @date 2022年07月04日 下午12:21
 * @do 请用一句话描述该类功能
 */
public enum REnum {
    // 用于不覆写，通用
    SUCCESS(0, "success"),
    // 用于不覆写，通用
    FAIL(1, "fail"),
    // 上传文件成功
    UPLOAD_FILE_SUCCESS(10000, "上传文件成功"),
    // 上传文件失败
    UPLOAD_FILE_FAIL(10001, "上传文件失败"),
    // 下载文件成功
    DOWNLOAD_FILE_SUCCESS(10002, "下载文件成功"),
    // 下载文件失败
    DOWNLOAD_FILE_FAIL(10003, "下载文件失败"),
    // 客户端链接失败
    MINIO_CLIENT_CONNECTION_FAIL(10004, "Minio客户端链接失败"),
    DELETE_FILE_SUCCESS(10005, "删除文件成功")
    ;


    private Integer statusCode;
    private String statusMsg;

    REnum(Integer statusCode, String statusMsg){
        this.statusCode = statusCode;
        this.statusMsg = statusMsg;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public String getStatusMsg() {
        return statusMsg;
    }
}
