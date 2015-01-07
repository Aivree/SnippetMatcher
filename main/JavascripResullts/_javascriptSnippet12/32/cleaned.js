 Drupal.behaviors.msfUsZenDropDownInline = {
    attach: function (context, settings) {
      var viewsRows = $(".view-events .views-row");
      var autocompleteSource = [];
      var topicList = $("#block-facetapi-gww0viyc3zhsopn1xdyz1wfggy1nfrrm");

      if ($(window).width() > 520) {
        $(".node-type-overview-page .block-facetapi .block-content", context).addClass("select-list");
      }

      // Add class to views-row when there is no summary image.
      $.each(viewsRows, function() {
        var summaryImage = $(this).find(".field-name-field-summary-image");
        if (!summaryImage.length) {
          $(this).addClass("no-summary-image");
        }
      });

      // Override autocomplete _renderMenu method.
      $.ui.autocomplete.prototype._renderMenu = function (ul, items) {
        ul.wrap("<div class='select-list'></div>").addClass("item-list");
        var self = this;
        $.each( items, function( index, item ) {
          self._renderItem( ul, item );
        });
      };

      // Override autocomplete _renderItem method.
      $.ui.autocomplete.prototype._renderItem = function (ul, item) {
        return $( "<li></li>" )
          .data( "item.autocomplete", item )
          .append( "<a href='" + item.href + "'>" + item.label + "</a>" )
          .appendTo( ul );
      };


      $.each($(".facetapi-facetapi-select-list li", topicList), function() {
        var link = $(this).find("a");
        autocompleteSource.push({
          label: link.clone().children().remove().end().text(),
          href: link.attr("href")
        });
      });

      // ADD AUTOCOMPLETE FUNCTIONALITY TO THE TOPIC FACET.
      $("<input>").attr({
          type: "text",
          class: "free-tags-select",
          value: Drupal.t("Topic")
      })
      .autocomplete({
        source: autocompleteSource,
        select: function(event, ui) {
          window.location = ui.item.href;
        }
      })
      .focus(function() {
        if ($(this).val() === Drupal.t("Topic")) {
          $(this).val("");
        }
      })
      .blur(function() {
        if ($(this).val() === "") {
          $(this).val(Drupal.t("Topic"));
        }
      })
      .appendTo(topicList);
    }
  };
