# Security Policy

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 1.0.x   | :white_check_mark: |

## Reporting a Vulnerability

We take the security of this project seriously. If you discover a security vulnerability, please follow these steps:

1. **Do not** open a public issue on GitHub
2. Send details to the project maintainers via the [GitHub Security Advisory](https://github.com/daanhan/ums-sdk/security/advisories/new) page
3. Include a detailed description of the vulnerability and steps to reproduce

We will acknowledge receipt of your report within 48 hours and provide an estimated timeline for a fix.

### What to Include

- Type of vulnerability
- Full details of the vulnerability
- Steps to reproduce
- Affected versions
- Any potential mitigations if known

### Our Commitment

- We will respond to your report within 48 hours
- We will keep you informed of the progress
- We will acknowledge your contribution when the fix is released
- We will not take legal action against security researchers acting in good faith

## Security Best Practices

When using this SDK, please follow these security guidelines:

1. **Never hardcode credentials**: Use environment variables or secure configuration management
2. **Use HTTPS only**: Ensure all communications use HTTPS
3. **Verify signatures**: Always verify payment callback signatures using `verifyNotification()`
4. **Keep dependencies updated**: Regularly update to the latest version of the SDK
5. **Protect notifyKey**: The notifyKey is critical for callback verification; store it securely

For more information, see the [Sensitive Information Management](README.md#sensitive-information-management) section in the README.