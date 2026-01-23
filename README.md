# HoutaStore E-commerce Testing Project 🛒

An automated testing framework for functional, performance, and cross-browser testing of the HaoutaStore.com e-commerce platform. Built with Selenium WebDriver and TestNG for comprehensive quality assurance.

## 📋 Project Overview

This academic project demonstrates automated testing practices for a real-world e-commerce website, covering critical user workflows from product discovery to checkout. The test suite validates functionality, performance metrics, and cross-browser compatibility.

**Website Under Test:** [HaoutaStore.com](https://haoutastore.com)

## 🎯 Test Coverage

### Functional Testing
- **Navigation Tests**: Menu navigation, category browsing, and URL validation
- **Product Search**: Search functionality with various input scenarios (valid products, empty searches, special characters)
- **Shopping Cart Operations**: Add to cart, remove from cart, quantity management
- **Product Filtering & Sorting**: Filter by category, price range, and sorting options
- **User Account Management**: Login, registration, authentication flows
- **Form Validation**: Contact forms, checkout forms, input validation

### Performance Testing
- Page load time monitoring (SLA: ≤5 seconds)
- DOM ready time measurement (SLA: ≤3 seconds)
- Resource loading performance
- JavaScript execution timing
- Memory usage tracking

### Cross-Browser Testing
- Google Chrome
- Mozilla Firefox
- Microsoft Edge

## 🛠️ Technologies & Tools

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 | Programming language |
| Selenium WebDriver | 4.19.1 | Browser automation |
| TestNG | 7.9.0 | Test framework & assertions |
| WebDriverManager | 6.1.0 | Automatic driver management |
| Allure | 2.27.0 | Test reporting |
| Maven | - | Build & dependency management |

## 📁 Project Structure

```
houta_test/
├── src/
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── test/
│       │           └── houta/
│       │               ├── BrowserCompatibilityTest.java
│       │               ├── CartAdditionTest.java
│       │               ├── CartRemovalTest.java
│       │               ├── ChromeTestBase.java
│       │               ├── FormTests.java
│       │               ├── NavigationTest.java
│       │               ├── PerformanceTest.java
│       │               ├── ProductFilterAndSortTest.java
│       │               ├── TestRecherche.java
│       │               └── UserAccountTests.java
│       └── resources/
│           └── testng/
│               └── testng.xml
├── rapport/
│   ├── complete_test_report.html
│   └── Rapport de Tests Complet - HaoutaStore.com.pdf
├── pom.xml
├── scenarios.xlsx
└── README.md
```

## 🚀 Getting Started

### Prerequisites

- **Java Development Kit (JDK)** 17 or higher
- **Maven** 3.6+ installed and configured
- **Internet connection** for WebDriverManager to download browser drivers
- Supported browsers installed: Chrome, Firefox, and/or Edge

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/AdibChiguer/houta_test.git
   cd houta_test
   ```

2. **Install dependencies**
   ```bash
   mvn clean install
   ```

## ▶️ Running Tests

### Run All Tests
```bash
mvn clean test
```

### Run Specific Test Suite
```bash
mvn test -Dtest=NavigationTest
mvn test -Dtest=PerformanceTest
mvn test -Dtest=BrowserCompatibilityTest
```

### Run Tests via TestNG XML
```bash
mvn test -DsuiteXmlFile=src/test/resources/testng/testng.xml
```

### Run Tests by Group
```bash
mvn test -Dgroups=navigation
mvn test -Dgroups=cart
mvn test -Dgroups=performance
```

## 📊 Test Reporting

### Generate Allure Report

1. **Run tests and generate Allure results**
   ```bash
   mvn clean test
   ```

2. **Generate and open Allure report**
   ```bash
   allure serve allure-results
   ```

### HTML Test Reports

After test execution, comprehensive HTML reports are available in:
- `rapport/complete_test_report.html`
- `complete_test_report.html`

These reports include:
- ✅ Test execution status (Pass/Fail)
- 📈 Performance metrics and benchmarks
- 📝 Detailed test steps and validations
- 🎨 Visual formatting for easy analysis

## 🧪 Test Classes Overview

| Test Class | Description | Key Features |
|------------|-------------|--------------|
| `ChromeTestBase` | Base class for all tests | WebDriver setup, implicit waits, teardown |
| `NavigationTest` | Menu and navigation validation | Hover actions, URL verification, category navigation |
| `TestRecherche` | Search functionality | Valid/invalid searches, empty input handling |
| `CartAdditionTest` | Add items to cart | Product selection, cart quantity validation |
| `CartRemovalTest` | Remove items from cart | Cart cleanup, empty cart verification |
| `ProductFilterAndSortTest` | Filtering and sorting | Price filters, category filters, sort options |
| `PerformanceTest` | Performance benchmarks | Load time, DOM ready time, resource timing |
| `BrowserCompatibilityTest` | Cross-browser testing | Chrome, Firefox, Edge compatibility |
| `UserAccountTests` | User authentication | Login, registration, session management |
| `FormTests` | Form validation | Input validation, error messages |

## 📈 Performance Metrics

The test suite monitors and validates the following performance indicators:

- **Page Load Time**: ≤ 5000ms
- **DOM Ready Time**: ≤ 3000ms
- **Resource Load Time**: Tracked and reported
- **JavaScript Execution Time**: Monitored
- **Memory Usage**: Performance impact assessment

## 🔧 Configuration

### TestNG Configuration (`testng.xml`)

The TestNG suite is configured to run multiple test classes with parameterization for cross-browser testing:

```xml
<suite name="HoutaStoreTestSuite">
    <test name="NavigationTest">
        <classes>
            <class name="com.test.houta.NavigationTest" />
        </classes>
    </test>
    <!-- Additional test configurations... -->
</suite>
```

### Maven Configuration (`pom.xml`)

The Maven Surefire plugin is configured to:
- Force TestNG provider
- Include all `*Test.java` and `*Tests.java` files
- Execute the TestNG suite from `testng.xml`

## ✅ Test Execution Best Practices

1. **Run tests in headless mode** for CI/CD integration (modify `ChromeTestBase`)
2. **Use groups** to run specific test categories
3. **Review Allure reports** for detailed test analytics
4. **Check performance metrics** against defined SLAs
5. **Verify cross-browser compatibility** before releases

## 📚 Learning Outcomes

This project demonstrates proficiency in:
- ✓ Selenium WebDriver automation
- ✓ TestNG test framework and annotations
- ✓ Page Object Model (implicit usage)
- ✓ Cross-browser testing strategies
- ✓ Performance testing with Selenium
- ✓ Test reporting with Allure
- ✓ Maven project management
- ✓ Quality assurance methodologies

## 🤝 Contributing

This is an academic project. Contributions, suggestions, and feedback are welcome!

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/improvement`)
3. Commit your changes (`git commit -m 'Add some improvement'`)
4. Push to the branch (`git push origin feature/improvement`)
5. Open a Pull Request

## 📝 License

This project is created for educational purposes as part of academic coursework.

## 👥 Authors

- **Ayoub** - Project Lead & Test Engineer
- **AdibChiguer** - Repository Owner

## 📧 Contact

For questions or feedback about this project, please open an issue on GitHub.

---

**Note**: This project is for educational and testing purposes only. All tests are performed on the publicly accessible HaoutaStore.com website.
