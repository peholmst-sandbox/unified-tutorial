Flow Tutorial
=============

A developer who completes the new Flow Tutorial will end up with this application. It is intended to demonstrate the
following things:

* A correct way of doing a CRUD user interface:
    * Navigation state is reflected in the URL
    * Good UX
    * Accessibility
* Confirmation (before delete and when navigating away from unsaved changes)
* Basic layouts
* Error handling
* `BeanValidationBinder`
* `Converter`s for domain primitives
* `Grid` (with lazy loading)
* `ComboBox`
* `MultiselectComboBox`
* `DatePicker`
* `LumoUtility`

The code is intended to be of production quality, but not fully polished. Security has intentionally been left out for
now to keep things simple.

## Wow! That's a lot of code to write!

The developer will not have to write everything from scratch. We will update the templates at *start.vaadin.com*
so that the master-detail view generates a lot of the code needed for the CRUD view. From now on, all developers who
want to build a CRUD master-detail view should be able to do it the proper way from the start.

## How do I run it?

Follow these steps:

1. Make sure you have at least Java 17 installed.
2. Check out the repository.
3. Run `./mvwn` in root directory of this tutorial.
4. Point your browser to http://localhost:8080

## Where is the tutorial?

We have not started to work on it yet.

## Does this demonstrate backend best practices?

Yes and no. The backend is intentionally kept simple, but it does follow our upcoming architecture recommendation.
That said, this approach may be perfectly fine for some applications, provided that security is added.

Additional articles and examples will be created for more advanced use cases, but they are outside the scope
of this tutorial.