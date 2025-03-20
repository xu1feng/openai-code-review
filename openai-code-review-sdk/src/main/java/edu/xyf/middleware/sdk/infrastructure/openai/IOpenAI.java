package edu.xyf.middleware.sdk.infrastructure.openai;

import edu.xyf.middleware.sdk.infrastructure.openai.dto.ChatCompletionRequestDTO;
import edu.xyf.middleware.sdk.infrastructure.openai.dto.ChatCompletionSyncResponseDTO;

/**
 * @author Xuyifeng
 * @description
 * @date 2025/3/20 16:00
 */

public interface IOpenAI {

    ChatCompletionSyncResponseDTO completions(ChatCompletionRequestDTO requestDTO) throws Exception;

}
