Drupal.behaviors.msfUsZenDropDownInline = {
    attach: function (context, settings) {
      if ($(window).width() > 520) {
        $('.node-type-overview-page .block-facetapi .block-content', context).addClass('select-list');
      }
      // Add class to views-row when there is no summary image.
      var viewsRows = $('.view-events .views-row');
      $.each(viewsRows, function() {
        var summaryImage = $(this).find('.field-name-field-summary-image');
        if (!summaryImage.length) {
          $(this).addClass('no-summary-image');
        }
      });
      // Override autocomplete _renderMenu method.
      $.ui.autocomplete.prototype._renderMenu = function (ul, items) {
        ul.wrap('<div class="select-list"></div>').addClass('item-list');
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
      // Add autocomplete functionality to the topic facet.
      var topicList = $('#block-facetapi-gww0viyc3zhsopn1xdyz1wfggy1nfrrm');
      topicList.prepend('<input type="text" value="' + Drupal.t('Topic') + '" class="free-tags-select" />');
      var autocompleteSource = [];
      $.each($('.facetapi-facetapi-select-list li', topicList), function() {
        var link = $(this).find('a');
        autocompleteSource.push({
          label: link.clone().children().remove().end().text(),
          href: link.attr('href')
        });
      });
      var freeTagsSelect = $('.free-tags-select');
      var defaultText = freeTagsSelect.val();
      freeTagsSelect.autocomplete({
        source: autocompleteSource,
        select: function(event, ui) {
          window.location = ui.item.href;
        }
      }).focus(function() {
        if (freeTagsSelect.val() == defaultText) {
          freeTagsSelect.val('');
        }
      }).blur(function() {
        if (freeTagsSelect.val() == '') {
          freeTagsSelect.val(defaultText);
        }
      });
    }
  };
