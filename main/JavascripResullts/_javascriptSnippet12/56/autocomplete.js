App.Views.Autocomplete = Backbone.View.extend({
  type: "autocomplete",
  tmpl: JST["js/app/templates/shared/autocomplete"],
  tagName: "div",
  events: {
  },

  initialize: function() {
    _.bindAll(this, "render", "handleInput");
  },

  handleInput: function(request, response) {
    var kwds = request.term, cached, self = this;
    cached = App.Views.Autocomplete.cache.get(kwds);
    if(!cached) {
      var c = new App.Collections.WorkLocations;
      c.fetch({
        data: { kwds: kwds },
        success: function() {
          App.Views.Autocomplete.cache.add({id: kwds, locs: c});
          response(c.models);    
        },
        select: function(ev, c) {
          self.$el.find("#work-location").val(c.get('zip'));
        },
      });
    } else {
      response(cached.locs);
    }
  },

  render: function() {
    var self = this;
    this.$el.html(this.tmpl());
    this.$el.find("#work-location").autocomplete({
      minLength: 2,
      source: this.handleInput,
      select: function(ev, el) {
        self.$el.find("#work-location").val(el.item.get("zip"));
        return false;
      },
    })
    .data("ui-autocomplete")._renderItem = function(ul, loc) { 
      return $("<li>")
       .append('<a>' + loc.get('zip') + ' ' + loc.get('city_transliterated') + '</a>')
       .appendTo(ul);
    }
    return this;
  },

  onClose: function() {
  }
});

App.Views.Autocomplete.cache = new App.Cache;
