package edu.xyf.middleware.sdk.domain.service;

import edu.xyf.middleware.sdk.infrastructure.git.GitCommand;
import edu.xyf.middleware.sdk.infrastructure.openai.IOpenAI;
import edu.xyf.middleware.sdk.infrastructure.weixin.WeiXin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.x509.AVA;


/**
 * @author Xuyifeng
 * @description
 * @date 2025/3/20 16:26
 */

public abstract class AbstractOpenAICodeReviewService implements IOpenAICodeReviewService{

    private final Logger logger = LoggerFactory.getLogger(AbstractOpenAICodeReviewService.class);

    protected final GitCommand gitCommand;
    protected final IOpenAI openAI;
    protected final WeiXin weiXin;

    protected AbstractOpenAICodeReviewService(GitCommand gitCommand, IOpenAI openAI, WeiXin weiXin) {
        this.gitCommand = gitCommand;
        this.openAI = openAI;
        this.weiXin = weiXin;
    }

    @Override
    public void exec() {
        try {
            // 1. 获取提交代码
            String diffCode = getDiffCode();
            // 2. 开始评审代码
            String recommend = codeReview(diffCode);
            // 3. 记录评审结果：返回日志结果
            String logUrl = recordCodeReview(recommend);
            // 4. 发送消息通知：日志地址、通知的内容
            putMessage(logUrl);
        } catch (Exception e) {
            logger.error("openai-code-review error: ", e);
        }
    }

    protected abstract String getDiffCode() throws Exception;

    protected abstract String recordCodeReview(String recommend) throws Exception;

    protected abstract String codeReview(String diffCode) throws Exception;

    protected abstract void putMessage(String logUrl) throws Exception;

}
