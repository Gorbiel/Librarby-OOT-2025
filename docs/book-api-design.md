# API Design: Adding Books to the Library

## Overview

This document describes the recommended approach for adding new books to the library system. The design follows REST principles and provides a flexible, maintainable solution.

## Entity Hierarchy

The book-related entities form a hierarchy:

```
Author (firstName, lastName)
    ↓
Book (title, genres, ageRating, authors)
    ↓
BookEdition (isbn, book, pageCount, publicationYear, publisher, language)
    ↓
ExactBookCopy (bookEdition, status)
```

## Recommended Approach: Separate Endpoints

### Why Separate Endpoints?

1. **Reusability**: Authors, books, and editions can be reused. If you want to add a new edition of an existing book, you don't need to recreate the author or book.

2. **Simplicity**: Each endpoint handles one responsibility, making the code easier to test and maintain.

3. **Flexibility**: Supports all use cases - adding a completely new book with a new author, adding a new edition of an existing book, or adding a new copy of an existing edition.

4. **REST Compliance**: Each resource has its own endpoint following REST conventions.

5. **Error Handling**: If something fails, it's clear which operation failed and why.

## Available Endpoints

### Authors (`/api/v1/authors`)
- `POST /api/v1/authors` - Create a new author
- `GET /api/v1/authors` - List all authors (supports `?lastName=` filter)
- `GET /api/v1/authors/{id}` - Get author by ID
- `DELETE /api/v1/authors/{id}` - Delete author

### Publishers (`/api/v1/publishers`)
- `POST /api/v1/publishers` - Create a new publisher
- `GET /api/v1/publishers` - List all publishers
- `GET /api/v1/publishers/{id}` - Get publisher by ID
- `DELETE /api/v1/publishers/{id}` - Delete publisher

### Books (`/api/v1/books`)
- `POST /api/v1/books` - Create a new book (references author IDs)
- `GET /api/v1/books` - List all books (supports `?title=` filter)
- `GET /api/v1/books/{id}` - Get book by ID
- `DELETE /api/v1/books/{id}` - Delete book

### Book Editions (`/api/v1/book-editions`)
- `POST /api/v1/book-editions` - Create a new edition (references book ID)
- `GET /api/v1/book-editions` - List all editions (supports `?bookId=` filter)
- `GET /api/v1/book-editions/{id}` - Get edition by ID
- `GET /api/v1/book-editions/isbn/{isbn}` - Get edition by ISBN
- `DELETE /api/v1/book-editions/{id}` - Delete edition

### Book Copies (`/api/v1/book-copies`)
- `POST /api/v1/book-copies` - Create a new physical copy (references edition ID)
- `GET /api/v1/book-copies` - List all copies (supports `?bookEditionId=` and `?availableOnly=` filters)
- `GET /api/v1/book-copies/{id}` - Get copy by ID
- `PATCH /api/v1/book-copies/{id}/status?status=` - Update copy status
- `DELETE /api/v1/book-copies/{id}` - Delete copy

## Typical Workflows

### Adding a Completely New Book

When adding a book with a new author:

```bash
# 1. Create the author
POST /api/v1/authors
{
  "firstName": "George",
  "lastName": "Orwell"
}
# Returns: { "id": 1, "firstName": "George", "lastName": "Orwell" }

# 2. Optionally create a publisher
POST /api/v1/publishers
{
  "name": "Penguin Books"
}
# Returns: { "id": 1, "name": "Penguin Books" }

# 3. Create the book
POST /api/v1/books
{
  "title": "1984",
  "genres": ["DYSTOPIAN", "SCIENCE_FICTION"],
  "ageRating": "TEENAGER",
  "authorIds": [1]
}
# Returns: { "id": 1, "title": "1984", ... }

# 4. Create a book edition
POST /api/v1/book-editions
{
  "isbn": "978-0451524935",
  "bookId": 1,
  "pageCount": 328,
  "publicationYear": 1961,
  "publisherId": 1,
  "language": "en"
}
# Returns: { "id": 1, "isbn": "9780451524935", ... }

# 5. Create physical copies
POST /api/v1/book-copies
{
  "bookEditionId": 1,
  "status": "AVAILABLE"
}
# Returns: { "id": 1, "bookEditionId": 1, ... }
```

### Adding a New Edition of an Existing Book

When the author and book already exist:

```bash
# 1. Search for the existing book
GET /api/v1/books?title=1984

# 2. Create the new edition (different ISBN)
POST /api/v1/book-editions
{
  "isbn": "978-0141036144",
  "bookId": 1,
  "pageCount": 400,
  "publicationYear": 2008,
  "publisherId": 1,
  "language": "en"
}

# 3. Create copies of the new edition
POST /api/v1/book-copies
{
  "bookEditionId": 2,
  "status": "AVAILABLE"
}
```

### Adding Another Copy of an Existing Edition

When you want to add more physical copies:

```bash
# 1. Find the edition by ISBN
GET /api/v1/book-editions/isbn/9780451524935

# 2. Create additional copies
POST /api/v1/book-copies
{
  "bookEditionId": 1
}
```

## Why Not a Single Endpoint?

A single endpoint that creates everything at once would have several issues:

1. **Duplicate Data**: What if the author already exists? You'd need complex logic to detect and reuse existing entities.

2. **Complex Request**: The request body would be deeply nested and harder to validate.

3. **Partial Failures**: If the book is created but the edition fails, what should happen?

4. **Less Flexibility**: Can't easily add a new edition without recreating the book.

5. **API Bloat**: You'd still need individual endpoints for updates and queries.

## Copy Status Values

- `AVAILABLE` - Ready for rental
- `BORROWED` - Currently with a reader
- `RESERVED` - Reserved, awaiting pickup
- `LOST` - Lost
- `UNAVAILABLE` - Temporarily unavailable (e.g., under repair)

## Summary

The separate endpoint approach provides maximum flexibility while keeping each component simple and testable. The typical workflow involves creating entities from the top of the hierarchy (Author) down to the bottom (ExactBookCopy), reusing existing entities when appropriate.
