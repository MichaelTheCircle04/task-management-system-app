package com.mtrifonov.task.management.system.app.assemblers;

import java.net.URI;
import java.util.stream.Stream;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.util.UriComponentsBuilder;

/**
*
* @Mikhail Trifonov
*/
public class PaginationUtils {

    public static void addPaginationLinks(Page<?> page, PagedModel<?> model, URI uri) {

		var builder = UriComponentsBuilder.fromUri(uri).queryParam("pageSize", page.getSize());
		var pageable = page.getPageable();
		
		var sort = Stream.of(page.getSort().toString().split(",")).map(s -> s.split(": ")).toList();
		for (var arr : sort) {
			var value = arr[0] + "," + arr[1].toLowerCase();
			builder.queryParam("sortParams", value);
		}

		builder.queryParam("pageNum", "{num}");

		if (page.hasPrevious()) {

			var first = Link.of(builder.buildAndExpand(0).toString(), IanaLinkRelations.FIRST);
			var prev = Link.of(builder.buildAndExpand(pageable.getPageNumber() - 1).toString(), IanaLinkRelations.PREV);
			model.add(first, prev);
		}

		var self = Link.of(builder.buildAndExpand(pageable.getPageNumber()).toString(), IanaLinkRelations.SELF);
		model.add(self);

		if (page.hasNext()) {
			var next = Link.of(builder.buildAndExpand(pageable.getPageNumber() + 1).toString(), IanaLinkRelations.NEXT);
			var last = Link.of(builder.buildAndExpand(page.getTotalPages() - 1).toString(), IanaLinkRelations.LAST);
			model.add(next, last);
		}
	}
}
