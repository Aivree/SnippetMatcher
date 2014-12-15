from django.db import models, IntegrityError
from django.db.models import Q

from apps.common.models import TimeStamped, Commentable, Taggable, Imageable
from apps.constants.models import CreditCardFee, WarehousingCost, PackagingCost


class Brand(TimeStamped):
    """
    a brand
    """

    name = models.CharField(max_length=255, unique=True)

    def __init__(self, *args, **kwargs):
        """calls the super class initializers - not a django thing: it's the model inheritance
        """
        super(Brand, self).__init__(*args, **kwargs)

    def __str__(self):
        """string representation of the model
        """
        return self.name


class LiveSkuManager(models.Manager):
    """
    custom manager that selects skus that are nested in live deals
    """

    def get_queryset(self):
        return Sku.objects.filter(
            id__in=Product.objects.filter(
                id__in=OfferedProduct.objects.filter(
                    offer__id__in=Deal.objects_live.all().values_list('offers__id', flat=True).distinct()
                ).values_list('product_id', flat=True).distinct()
            ).values_list('sku_id', flat=True).distinct())


class DeadSkuManager(models.Manager):
    """
    custom manager that selects skus that are not in live deals
    """

    def get_queryset(self):
        return Sku.objects.filter(~Q(
            id__in=Product.objects.filter(
                id__in=OfferedProduct.objects.filter(
                    offer__id__in=Deal.objects_done.all().values_list('offers__id', flat=True).distinct()
                ).values_list('product_id', flat=True).distinct()
            ).values_list('sku_id', flat=True).distinct()))


class Sku(TimeStamped, Imageable):
    """
    a stock keeping unit; a single piece of something we sell. i.e. a single bullystick
    """

    id = models.IntegerField(blank=True, primary_key=True)
    upc = models.CharField(max_length=255, blank=True, null=True, default=None, db_index=True)
    name = models.CharField(max_length=255, db_index=True)
    brand = models.ForeignKey('products.Brand', db_index=True)
    vendor = models.ForeignKey('vendors.Vendor', db_index=True)
    vendor_part_number = models.CharField(max_length=255, blank=True, null=True, default=None)
    # warehouse
    requires_assembly = models.BooleanField(default=False)
    is_warehouse_consumable = models.BooleanField(default=False)
    # attributes
    weight = models.FloatField()
    color = models.CharField(max_length=255, blank=True, null=True, default=None, db_index=True)
    flavor = models.CharField(max_length=255, blank=True, null=True, default=None, db_index=True)
    size = models.CharField(max_length=255, blank=True, null=True, default=None, db_index=True)
    style = models.CharField(max_length=255, blank=True, null=True, default=None, db_index=True)
    force_ground_shipment = models.BooleanField(default=False)
    expiration_date = models.CharField(max_length=255, blank=True, null=True, default=None)
    country_of_origin = models.CharField(max_length=255, blank=True, null=True, default=None)
    ingredients = models.CharField(max_length=255, blank=True, null=True, default=None)
    # custom managers
    objects = models.Manager()
    objects_live = LiveSkuManager()
    objects_dead = DeadSkuManager()

    def __init__(self, *args, **kwargs):
        """calls the super class initializers - not a django thing: it's the model inheritance
        """
        super(Sku, self).__init__(*args, **kwargs)

    def __str__(self):
        """string representation of the model
        """
        output = '[%s] %s %s' % (self.id, self.brand.name, self.name)
        attributes = [str(x) for x in (self.color, self.style, self.flavor, self.size) if x is not None]
        if len(attributes):
            output += ' (%s)' % ', '.join(attributes)
        return output

    def save(self, *args, **kwargs):
        """if an id is not present, select the maximum id value from the database and increment it and set that as
        the id value for the sku
        """
        if not self.id:
            self.id = int(Sku.objects.all().aggregate(models.Max('id'))['id__max']) + 1
        super(Sku, self).save(*args, **kwargs)

    def available_for_sale(self):
        """grabs the availability from the 1to1 relationship with the inventory
        """
        return self.inventory.available_for_sale()

    def _image_links(self):
        """returns a list of link attributes for related `common.Image` instances
        """
        return [img.link for img in self.images.all()]


class Product(TimeStamped):
    """
    a sellable object made of skus
    """

    DEFAULT_SKUS_PER = 1
    DEFAULT_ADDITIONAL_WEIGHT = 0

    name = models.CharField(max_length=255, blank=True, db_index=True)
    sku = models.ForeignKey('products.Sku', db_index=True)
    skus_per = models.IntegerField(default=DEFAULT_SKUS_PER)
    # warehouse items
    packaging = models.ForeignKey('products.Sku', related_name='as_packaging', blank=True, null=True, default=None)
    additional_weight = models.FloatField(default=DEFAULT_ADDITIONAL_WEIGHT)

    def __init__(self, *args, **kwargs):
        """calls the super class initializers - not a django thing: it's the model inheritance
        """
        super(Product, self).__init__(*args, **kwargs)

    def __str__(self):
        """string representation of the model
        """
        return self.name

    def get_weight(self):
        """computes the weight of the product including additional weight estimates and packaging
        """
        try:
            return self.sku.weight * self.skus_per + self.additional_weight + self.packaging.weight
        except AttributeError:
            return self.sku.weight * self.skus_per + self.additional_weight

    def available_for_sale(self):
        """computes the floor of the sku's availability by skus per
        """
        return self.sku.available_for_sale() // self.skus_per


class Offer(TimeStamped, Imageable):
    """
    what customers select for purchase
    """

    title = models.CharField(max_length=255, db_index=True)
    price = models.FloatField(db_index=True)
    description = models.TextField(blank=True)

    def __init__(self, *args, **kwargs):
        """calls the super class initializers - not a django thing: it's the model inheritance
        """
        super(Offer, self).__init__(*args, **kwargs)

    def __str__(self):
        """string representation of the model
        """
        return self.title

    def get_weight(self):
        """gets the sum of the weight of the products
        """
        try:
            return sum(op.get_weight() for op in self.offeredproduct_set.all())
        except TypeError:
            return 0

    def _image_links(self):
        """returns a list of link attributes for related `common.Image` instances
        """
        return [img.link for img in self.images.all()]

    def add_product(self, product, products_per=None):
        """quickly creates a `products.OfferedProduct` record related to this instance

        :param product: {products.Product} an instance of a product
        :param products_per: {int|None} the number of products wrapped in the offer
        :return: the created OfferedProduct
        """
        if not products_per:
            products_per = OfferedProduct.DEFAULT_PRODUCTS_PER
        op = OfferedProduct(
            offer=self,
            product=product,
            products_per=products_per
        )
        try:
            op.save()
        except IntegrityError:
            op = OfferedProduct.objects.get(offer=self, product=product, products_per=products_per)
        return op

    def available_for_sale(self):
        """finds the minimum availability for all products
        """
        try:
            return min(op.available_for_sale() for op in self.offeredproduct_set.all())
        except ValueError:
            return 0

    def _current_offered_products(self):
        """private method for building out read-only members in the serializer
        """
        return [op for op in self.offeredproduct_set.all()]

    def _country_of_origin(self):
        coo = []
        for op in self._current_offered_products():
            country = op.product.sku.country_of_origin
            if country not in coo and country is not None:
                coo.append(country)
        if not len(coo):
            return None
        return ', '.join(coo)

    def _ingredients(self):
        ingredients = []
        for op in self._current_offered_products():
            ingrs = str(op.product.sku.ingredients).split(',')
            for ingr in ingrs:
                if ingr not in ingredients and ingr != "None":
                    ingredients.append(ingr)
        if not len(ingredients):
            return None
        return ', '.join(ingredients)



class OfferedProduct(TimeStamped):
    """
    a wrapper for a product as an offer
    """

    DEFAULT_PRODUCTS_PER = 1

    offer = models.ForeignKey('products.Offer')
    product = models.ForeignKey('products.Product')
    products_per = models.IntegerField(default=DEFAULT_PRODUCTS_PER)

    class Meta:
        unique_together = (
            ('offer', 'product', 'products_per'),
        )

    def __init__(self, *args, **kwargs):
        """calls the super class initializers - not a django thing: it's the model inheritance
        """
        super(OfferedProduct, self).__init__(*args, **kwargs)

    def __str__(self):
        """string representation of the model
        """
        return '%s : %s of %s' % (str(self.offer)[:40], self.products_per, str(self.product))

    def get_weight(self):
        """computes the weight of the offering
        """
        return self.product.get_weight() * self.products_per

    def available_for_sale(self):
        """computes the floor of the product's availability by products per
        """
        return self.product.available_for_sale() // self.products_per


class LiveDealManager(models.Manager):
    """
    a custom manager for retrieving live deals
    """

    def get_queryset(self):
        return super(LiveDealManager, self).get_queryset().filter(is_live=True)


class DoneDealManager(models.Manager):
    """
    a custom manager for retrieving done deals
    """

    def get_queryset(self):
        return super(DoneDealManager, self).get_queryset().filter(is_done=True)


class Deal(TimeStamped, Taggable, Imageable):
    """
    presentation wrapper for offers
    """

    MINIMUM_INVENTORY_FOR_EXTENSION = 10
    DEFAULT_INITIAL_POSITION = '1000'

    # carry over primary keys
    id = models.IntegerField(blank=True, primary_key=True)
    # attach to user
    buyer = models.ForeignKey('vauth.User')
    from_proposal = models.ForeignKey('products.Proposal', blank=True, null=True, default=None)
    credit_card_fee = models.ForeignKey('constants.CreditCardFee', blank=True)
    warehousing_cost = models.ForeignKey('constants.WarehousingCost', blank=True)
    packaging_cost = models.ForeignKey('constants.PackagingCost', blank=True)
    state = models.CharField(max_length=244)
    vendor = models.ForeignKey('vendors.Vendor', blank=True, null=True, default=None)
    vendor_link = models.URLField(max_length=1024, blank=True, null=True, default=None)
    # offers
    offers = models.ManyToManyField('products.Offer', blank=True)
    # display fields
    title = models.CharField(max_length=255, db_index=True)
    title_external = models.CharField(max_length=255, blank=True, null=True, default=None, db_index=True)
    title_external_short = models.CharField(max_length=255, blank=True, null=True, default=None, db_index=True)
    description = models.TextField(blank=True)
    initial_position = models.CharField(max_length=4, default=DEFAULT_INITIAL_POSITION)
    current_position = models.CharField(max_length=4, blank=True)
    email_position = models.CharField(max_length=4, default=DEFAULT_INITIAL_POSITION)
    display_groups = models.ManyToManyField('customers.DisplayGroup', blank=True)
    msrp = models.FloatField(blank=True, null=True, default=None)
    # detail options
    includes = models.TextField(blank=True)
    shipping = models.CharField(max_length=255)
    # customer options
    customer_chooses = models.BooleanField(default=True)
    offers_are_subscribable = models.BooleanField(default=False)
    offers_subscribable_only = models.BooleanField(default=False)
    # time indicators
    starts_on = models.DateTimeField(db_index=True, blank=True, null=True, default=None)
    ends_on = models.DateTimeField(db_index=True, blank=True, null=True, default=None)
    # warehouse fields
    after_ends_do = models.TextField(blank=True)
    is_drop_shipment = models.BooleanField(default=False)
    # query helpers
    is_live = models.BooleanField(default=False, db_index=True)
    is_done = models.BooleanField(default=False, db_index=True)
    # custom managers
    objects = models.Manager()
    objects_live = LiveDealManager()
    objects_done = DoneDealManager()

    def __init__(self, *args, **kwargs):
        """calls the super class initializers - not a django thing: it's the model inheritance
        """
        super(Deal, self).__init__(*args, **kwargs)

    def __str__(self):
        """string representation of the model
        """
        return self.title

    def save(self, *args, **kwargs):
        """if an id is not present, select the maximum id value from the database and increment it and set that as
        the id value for the sku
        """
        if not self.id:
            self.id = int(Deal.objects.all().aggregate(models.Max('id'))['id__max']) + 1
        if not self.credit_card_fee:
            self.credit_card_fee = CreditCardFee.get_current_fee()
        if not self.warehousing_cost:
            self.warehousing_cost = WarehousingCost.get_current_cost()
        if not self.packaging_cost:
            self.packaging_cost = PackagingCost.get_current_cost()
        super(Deal, self).save(*args, **kwargs)

    def get_cogs(self):
        """computes the cost of goods sold
        """
        base_cost = self.shipping_cost + self.product_cost + self.warehousing_cost.cost + self.packaging_cost.cost
        fees = self.credit_card_fee.rate * self.our_price
        return base_cost + fees

    def get_contribution_margin(self):
        """computes the contribution margin
        """
        return (self.our_price - self.get_cogs()) / self.our_price

    def _current_offers(self):
        """private method for building out read-only members in the serializer
        """
        return [o for o in self.offers.all()]

    def _current_products(self):
        """private method for building out read-only members in the serializer
        """
        return [op.product for o in self._current_offers() for op in o.offeredproduct_set.all()]

    def _current_skus(self):
        """private method for building out read-only members in the serializer
        """
        return [product.sku for product in self._current_products()]

    def _current_display_groups(self):
        """private method for building out read-only members in the serializer
        """
        return [dg for dg in self.display_groups.all()]

    def _get_brand(self):
        skus = self._current_skus()
        try:
            return skus[0].brand.name
        except IndexError:
            return None

    def _image_links(self):
        """returns a list of link attributes for related `common.Image` instances
        """
        return [str(img) for img in self.images.all()]


class Proposal(TimeStamped, Commentable, Imageable):
    """
    deal proposals
    """

    # attach to user
    buyer = models.ForeignKey('vauth.User')
    # presentation fields
    title = models.CharField(max_length=255)
    review_on = models.DateField(blank=True, null=True, default=None)
    state = models.CharField(max_length=255, blank=True, null=True, default=None)
    # vendor information
    vendor = models.ForeignKey('vendors.Vendor')
    vendor_url = models.URLField(max_length=1024, blank=True, null=True, default=None)
    is_drop_shipment = models.BooleanField(default=False)  # replaces inventory, etc. we either stock it or we don't.
    msrp = models.FloatField()
    # pricing
    our_price = models.FloatField(blank=True, null=True, default=None)
    amazon_price = models.FloatField(blank=True, null=True, default=None)
    amazon_price_with_shipping = models.FloatField(blank=True, null=True, default=None)
    competitor_price = models.FloatField(blank=True, null=True, default=None)
    competitor_price_with_shipping = models.FloatField(blank=True, null=True, default=None)
    # costs
    shipping_cost = models.FloatField(blank=True, default=0)
    product_cost = models.FloatField(blank=True, default=0)
    credit_card_fee = models.ForeignKey('constants.CreditCardFee', blank=True)
    warehousing_cost = models.ForeignKey('constants.WarehousingCost', blank=True)
    packaging_cost = models.ForeignKey('constants.PackagingCost', blank=True)
    # details
    skus_per_offer = models.IntegerField(blank=True, null=True, default=None)
    sku_weight = models.FloatField(blank=True, null=True, default=None)
    packaging_weight = models.FloatField(blank=True, null=True, default=None)
    amazon_url = models.URLField(blank=True, null=True, default=None)
    competitor_url = models.URLField(blank=True, null=True, default=None)
    amazon_prime_eligible = models.NullBooleanField(default=None)

    def __init__(self, *args, **kwargs):
        """calls the super class initializers - not a django thing: it's the model inheritance
        """
        super(Proposal, self).__init__(*args, **kwargs)

    def __str__(self):
        """string representation of the model
        """
        return self.title

    def save(self, *args, **kwargs):
        """sets the constant foreign keys to current if not set; runs super
        """
        if not self.credit_card_fee:
            self.credit_card_fee = CreditCardFee.get_current_fee()
        if not self.warehousing_cost:
            self.warehousing_cost = WarehousingCost.get_current_cost()
        if not self.packaging_cost:
            self.packaging_cost = PackagingCost.get_current_cost()
        super(Proposal, self).save(*args, **kwargs)

    def get_cogs(self):
        """computes the cost of goods sold
        """
        base_cost = self.shipping_cost + self.product_cost + self.warehousing_cost.cost + self.packaging_cost.cost
        fees = self.credit_card_fee.rate * self.our_price
        return base_cost + fees

    def get_contribution_margin(self):
        """computes the contribution margin
        """
        return (self.our_price - self.get_cogs()) / self.our_price
