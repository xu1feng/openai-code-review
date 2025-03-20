package edu.xyf.middleware.sdk;


import edu.xyf.middleware.sdk.domain.service.impl.OpenAICodeReviewService;
import edu.xyf.middleware.sdk.infrastructure.git.GitCommand;
import edu.xyf.middleware.sdk.infrastructure.openai.IOpenAI;
import edu.xyf.middleware.sdk.infrastructure.openai.impl.ChatGLM;
import edu.xyf.middleware.sdk.infrastructure.weixin.WeiXin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenAICodeReview {

    private static final Logger logger = LoggerFactory.getLogger(OpenAICodeReview.class);

    // 微信配置
    private String weixin_appid = "wxc16ffef8ada4e8e2";
    private String weixin_secret = "7e23cb0541935a6f040127df737858fe";
    private String weixin_touser = "ov-g46fQie9ar7VVZ_OGaNZg7Ow0";
    private String weixin_template_id = "XThwNxD1dBAJ9wPRttlL89EPxthE1mQasmEpJ58IbIQ";

    // ChatGLM 配置
    private String chatglm_apiHost = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
    private String chatglm_apiKeySecret = "";

    // Github 配置
    private String github_review_log_uri;
    private String github_token;

    // 工程配置 - 自动获取
    private String github_project;
    private String github_branch;
    private String github_author;

    public static void main(String[] args) throws Exception {
        GitCommand gitCommand = new GitCommand(
                getEnv("GITHUB_REVIEW_LOG_URI"),
                getEnv("GITHUB_TOKEN"),
                getEnv("COMMIT_PROJECT"),
                getEnv("COMMIT_BRANCH"),
                getEnv("COMMIT_AUTHOR"),
                getEnv("COMMIT_MESSAGE")
        );

        /**
         * ⚙️项目：{{repo_name.DATA}} 💡分支：{{branch_name.DATA}} ✏️作者：{{commit_author.DATA}} 📝说明：{{commit_message.DATA}}
         */
        WeiXin weiXin = new WeiXin(
                getEnv("WEIXIN_APPID"),
                getEnv("WEIXIN_SECRET"),
                getEnv("WEIXIN_TOUSER"),
                getEnv("WEIXIN_TEMPLATE_ID")
        );

        IOpenAI openai = new ChatGLM(getEnv("CHATGLM_APIHOST"), getEnv("CHATGLM_APIKEYSECRET"));

        OpenAICodeReviewService openAICodeReviewService = new OpenAICodeReviewService(gitCommand, openai, weiXin);
        openAICodeReviewService.exec();

        logger.info("openai-code-review done!");
    }

    private static String getEnv(String key) {
        String value = System.getenv(key);
        if (null == value || value.isEmpty()) {
            throw new RuntimeException("token is null");
        }
        return value;
    }

}
