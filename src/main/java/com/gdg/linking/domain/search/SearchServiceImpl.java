package com.gdg.linking.domain.search;
import com.gdg.linking.domain.folder.Folder;
import com.gdg.linking.domain.folder.FolderRepository;
import com.gdg.linking.domain.item.Item;
import com.gdg.linking.domain.item.ItemRepository;
import com.gdg.linking.domain.search.dto.response.SearchResponse;
import com.gdg.linking.domain.tag.Tag;
import com.gdg.linking.domain.tag.TagRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class SearchServiceImpl implements SearchService {


    private final ItemRepository itemRepository;
    private final FolderRepository folderRepository;
    private final TagRepository tagRepository;


    @Override
    @Transactional(readOnly = true) // 단순 조회이므로 readOnly 설정
    public SearchResponse search(String keyword, int page, int size,Long userId) {
        PageRequest pageRequest = PageRequest.of(page, size);

        // 1. 게시글 검색 (Slice)
        Slice<Item> itemSlice = itemRepository.searchItemsByKeyword(userId, keyword, pageRequest);

        var itemContent = itemSlice.getContent().stream()
                .map(i -> SearchResponse.ItemSummaryDto.builder()
                        .itemId(i.getItemId())
                        .title(i.getTitle())
                        .folderName(i.getFolder() != null ? i.getFolder().getFolderName() : null)
                        .build())
                .toList();

        // 2. 폴더 검색 (Slice)
        Slice<Folder> folderSlice = folderRepository.findByUser_UserIdAndFolderNameContaining(userId, keyword, pageRequest);
        var folderContent = folderSlice.getContent().stream()
                .map(f -> SearchResponse.FolderSummaryDto.builder()
                        .folderId(f.getFId())
                        .folderName(f.getFolderName())
                        .build())
                .toList();

        // 3. 태그 검색 (Slice)
        Slice<Tag> tagSlice = tagRepository.findTagsByUserIdAndKeyword(userId, keyword, pageRequest);
        var tagContent = tagSlice.getContent().stream()
                .map(t -> SearchResponse.TagSummaryDto.builder()
                        .tagId(t.getTagId())
                        .tagName(t.getTagName())
                        .build())
                .toList();

        return SearchResponse.builder()
                .items(SearchResponse.SearchSlice.<SearchResponse.ItemSummaryDto>builder()
                        .content(itemContent).hasNext(itemSlice.hasNext()).currentPage(page).build())
                .folders(SearchResponse.SearchSlice.<SearchResponse.FolderSummaryDto>builder()
                        .content(folderContent).hasNext(folderSlice.hasNext()).currentPage(page).build())
                .tags(SearchResponse.SearchSlice.<SearchResponse.TagSummaryDto>builder()
                        .content(tagContent).hasNext(tagSlice.hasNext()).currentPage(page).build())
                .build();
    }
}
