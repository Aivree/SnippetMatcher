from django.db.models.signals import pre_save, post_save #, post_init
from django.dispatch import receiver

from .models import Inventory, Location, InventoryChange, LocationChange
from apps.products.models import Sku


@receiver(post_save, sender=Sku)
def create_inventory_for_new_sku(sender, instance, created, **kwargs):
    """after a sku is created, create an inventory
    """
    if created:
        inventory = Inventory(sku=instance)
        inventory.save()


@receiver(post_save, sender=Sku)
def create_location_for_new_sku(sender, instance, created, **kwargs):
    """after a sku is created, create a location
    """
    if created:
        location = Location(sku=instance)
        location.save()


# @receiver(post_init, sender=Inventory)
# def set_inventory_initial_on_shelves(sender, instance, **kwargs):
#     """after an inventory is initialized, set its initial on shelves value
#     """
#     print('post_init triggered')
#     instance.__initial_on_shelves = instance.on_shelves


@receiver(pre_save, sender=InventoryChange)
def update_inventory_change_fields(sender, instance, **kwargs):
    """before an inventory change is saved, update its fields
    """
    instance.old_on_shelves = instance.inventory.on_shelves
    if instance.delta:
        instance.new_on_shelves = instance.old_on_shelves + instance.delta
    elif instance.new_on_shelves:
        instance.delta = instance.new_on_shelves - instance.old_on_shelves
    else:
        raise ValueError('InventoryChange requires either delta or new_on_shelves to be set')


@receiver(post_save, sender=InventoryChange)
def update_inventory_after_change(sender, instance, created, **kwargs):
    """after an inventory change is saved, update its related inventory
    """
    if created:
        instance.inventory.on_shelves = instance.new_on_shelves
        instance.inventory.save(from_inventory_change=True)


@receiver(pre_save, sender=LocationChange)
def update_location_change_fields(sender, instance, **kwargs):
    """before a location change is saved, set its old location
    """
    instance.old_location = instance.location.location
    try:
        if not len(instance.new_location):
            instance.new_location = None
    except TypeError:
        instance.new_location = None


@receiver(post_save, sender=LocationChange)
def update_location_after_change(sender, instance, **kwargs):
    """after a location change is saved, update its relative location
    """
    instance.location.location = instance.new_location
    instance.location.save(from_location_change=True)
