FROM postgres:16-alpine

ENV POSTGRES_DB=vocabulary_db
ENV POSTGRES_USER=vocabulary_user
ENV POSTGRES_PASSWORD=vocabulary_password

EXPOSE 5432

# Add any custom initialization scripts if needed
# COPY ./init-scripts/ /docker-entrypoint-initdb.d/

# Health check to verify the database is running
HEALTHCHECK --interval=5s --timeout=5s --retries=3 CMD pg_isready -U vocabulary_user -d vocabulary_db || exit 1
