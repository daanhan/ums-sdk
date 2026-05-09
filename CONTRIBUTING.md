# Contributing to ChinaUMS Payment SDK

We welcome contributions from the community! This document describes the process for contributing to this project.

## Code of Conduct

By participating in this project, you agree to abide by our [Code of Conduct](CODE_OF_CONDUCT.md).

## How to Contribute

### Reporting Bugs

1. Check the [Issue Tracker](https://github.com/daanhan/ums-sdk/issues) to see if the bug has already been reported
2. If not, create a new issue with a clear title and description
3. Include detailed steps to reproduce the issue
4. Include the version of the SDK and Java environment you are using
5. Include any relevant logs or error messages

### Suggesting Features

1. Check the [Issue Tracker](https://github.com/daanhan/ums-sdk/issues) for existing feature requests
2. Create a new issue with the label "enhancement"
3. Describe the feature and its use case in detail
4. Explain why this feature would be beneficial to the community

### Submitting Changes

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Make your changes following our coding conventions
4. Write or update tests to cover your changes
5. Ensure all tests pass
6. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
7. Push to the branch (`git push origin feature/AmazingFeature`)
8. Open a Pull Request

## Development Setup

### Prerequisites

- Java 1.8 or higher
- Maven 3.6+

### Build and Test

```bash
# Build the project
mvn clean compile

# Run all tests
mvn clean test

# Run tests with coverage report
mvn clean test jacoco:report

# Package the project
mvn clean package
```

## Coding Conventions

- Follow standard Java naming conventions
- Use camelCase for variable and method names
- Use PascalCase for class names
- Use UPPER_SNAKE_CASE for constants
- Keep methods small and focused on a single responsibility
- Write Javadoc comments for public APIs
- Maintain unit test coverage above 80%

## Pull Request Guidelines

- Keep PRs focused on a single concern
- Provide a clear description of the changes
- Reference any related issues
- Ensure all CI checks pass
- Update documentation if needed
- Add tests for new functionality

## License

By contributing, you agree that your contributions will be licensed under the MIT License.