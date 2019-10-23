# Improved CircleCI Status badges

Provides status badges for circleCI projects, ignoring skipped or cancelled builds and also indicating if there is 
another build running or queued.

Configurable parameters (via environment variables):

| variable          | default value            |
| --------          | -------------            |
| ORGANISATION_NAME | DepartmentOfHealth-htbhf |
| REPOSITORY_BRANCH | master                   |
| CIRCLECI_TOKEN    | none (required)          |

Designed to be deployed to cloudfoundry:

```$bash
./gradlew clean build
cf push --var circleci-token=api-token-value
```

Once deployed, calls to `https://htbhf-circleci-status.london.cloudapps.digital/repository-name` should return a status image.
