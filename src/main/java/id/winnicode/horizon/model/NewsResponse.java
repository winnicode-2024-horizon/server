package id.winnicode.horizon.model;

import id.winnicode.horizon.data.entity.News;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewsResponse {

    private int size;

    private List<News> news;
}
