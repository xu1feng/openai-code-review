package edu.xyf.middleware.sdk.domain.service.impl;

import edu.xyf.middleware.sdk.domain.model.Model;
import edu.xyf.middleware.sdk.domain.service.AbstractOpenAICodeReviewService;
import edu.xyf.middleware.sdk.infrastructure.git.GitCommand;
import edu.xyf.middleware.sdk.infrastructure.openai.IOpenAI;
import edu.xyf.middleware.sdk.infrastructure.openai.dto.ChatCompletionRequestDTO;
import edu.xyf.middleware.sdk.infrastructure.openai.dto.ChatCompletionSyncResponseDTO;
import edu.xyf.middleware.sdk.infrastructure.weixin.WeiXin;
import edu.xyf.middleware.sdk.infrastructure.weixin.dto.TemplateMessageDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class OpenAICodeReviewService extends AbstractOpenAICodeReviewService {

    public OpenAICodeReviewService(GitCommand gitCommand, IOpenAI openAI, WeiXin weiXin) {
        super(gitCommand, openAI, weiXin);
    }

    @Override
    protected String getDiffCode() throws Exception {
        return gitCommand.diff();
    }

    @Override
    protected String recordCodeReview(String recommend) throws Exception {
        return gitCommand.commitAndPush(recommend);
    }

    @Override
    protected String codeReview(String diffCode) throws Exception {
        ChatCompletionRequestDTO chatCompletionRequest = new ChatCompletionRequestDTO();
        chatCompletionRequest.setModel(Model.GLM_4_FLASH.getCode());
        chatCompletionRequest.setMessages(new ArrayList<ChatCompletionRequestDTO.Prompt>() {
            private static final long serialVersionUID = -7988151926241837899L;

            {
                add(new ChatCompletionRequestDTO.Prompt("user", "你是一个高级编程架构师，精通各类场景方案、架构设计和编程语言请，请您根据git diff记录，对代码做出评审。代码如下:"));
                add(new ChatCompletionRequestDTO.Prompt("user", diffCode));
            }
        });

        ChatCompletionSyncResponseDTO completions = openAI.completions(chatCompletionRequest);
        ChatCompletionSyncResponseDTO.Message message = completions.getChoices().get(0).getMessage();
        return message.getContent();
    }

    @Override
    protected void putMessage(String logUrl) throws Exception {
        Map<String, Map<String, String>> data = new HashMap<>();
        TemplateMessageDTO.put(data, TemplateMessageDTO.TemplateKey.REPO_NAME, gitCommand.getProject());
        TemplateMessageDTO.put(data, TemplateMessageDTO.TemplateKey.BRANCH_NAME, gitCommand.getBranch());
        TemplateMessageDTO.put(data, TemplateMessageDTO.TemplateKey.COMMIT_AUTHOR, gitCommand.getAuthor());
        TemplateMessageDTO.put(data, TemplateMessageDTO.TemplateKey.COMMIT_MESSAGE, gitCommand.getMessage());
        TemplateMessageDTO.put(data, TemplateMessageDTO.TemplateKey.REVIEW, logUrl);
        weiXin.sendTemplateMessage(logUrl, data);
    }

}
