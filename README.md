# FourthWall
<img src="https://github.com/user-attachments/assets/74967d97-bc78-4e95-b357-71fd3a99cfb3" width="96" height="96" alt="Home Screen">

FourthWall is a brilliant decentralized wallet application. Our solution aims to provide a seamless, secure, and user-centric experience for payments. We empower you to send money across the globe. And fast!

## Table of Contents
- [Team](#team)
- [Overview](#overview)
- [Key Features](#key-features)
- [Design Considerations](#design-considerations)
- [App Screenshots](#app-screenshots)
- [Future Plans / Roadmap](#future-plans--roadmap)
- [Current Limitations and Known Issues](#current-limitations-and-known-issues)
- [Technical Stack](#technical-stack)
- [Getting Started](#getting-started)
- [Contributing](#contributing)
- [License](#license)
- [Acknowledgements](#acknowledgements)

## Team

| Name | Role | Email |
|------|------|-------|
| Judah Ben | Application Developer | judahben149@gmail.com |
| Rosemary Udele | Product Manager | rejiroghene@gmail.com |

## Overview

FourthWall is an Android wallet application built with Kotlin. 
It leverages the power of decentralized technologies via the TBDex SDK to offer a secure, transparent, and user-friendly platform for making payments globally.

## Key Features

- Secure wallet creation and management
- Simple flow and top-tier user experience
- Multi-currency support
- Best-rate currency exchange
- User-driven PFI ratings
- Transparent fee structure
- Enhanced security with Biometric login.

## Design Considerations

### Profitability

FourthWall charges a fee of 1.5% on currency exchanges, which is transparent and clearly communicated before each transaction. This fee allows us to facilitate smooth and reliable channels between participating financial insritutions and other parties. 

By keeping our fee structure transparent, users can confidently make informed decisions, without any hidden charges.

### Optionality

FourthWall employs a sorting algorithm to compare all available offerings from PFIs and pre-selects the best option for the user. This aims at reducing the cognitive load and improving user experience.
Users can further explore to see exchange rates and user ratings for each PFI ensuring that they choose the most reliable option. 

When multiple PFIs offer the same rate, customer satisfaction and feedback come into play, highlighting the providers with the best track record. This helps the users to simplify the decision-making process.


### Customer Management
For us at FourthWall, data privacy is at the forefront of our operations. The app enables users to securely generate and store their Decentralized Identifiers (DIDs) and Verifiable Credentials (VCs), which are linked to their identity. We ensure that only the user has control over their VCs, which based on the user’s authorization can be easily shared with PFIs during transactions.

Fourthwall users have full control over their credentials. They can choose to unlink or revoke credentials at any time (from the profile option), giving them the flexibility to manage their digital identity on their own terms.


### Customer Satisfaction

At FourthWall, we take customer satisfaction seriously. After each transaction, users are prompted to rate their experience with the PFI. This feedback is aggregated, and each PFI is assigned a score based on user reviews. These scores are updated and play a critical role in the user’s decision-making process when selecting a PFI for future transactions.

Also, we believe in maintaining high standards for the services offered through our app. PFIs with consistently low ratings will be flagged and, if necessary, removed from our platform to ensure that only trusted and user-centric PFIs are onboarded.

   


## App Screenshots

#### Home Screen
<img src="https://github.com/user-attachments/assets/c6d90e18-d1f3-4cdf-8d9c-baf772115548" width="250" alt="Home Screen (Empty)">
<img src="https://github.com/user-attachments/assets/2b32ab93-1755-49e3-975f-870be14bd8f4" width="250" alt="Home Screen">

#### Offerings Selection Flow
<img src="https://github.com/user-attachments/assets/dbdce84f-b811-4f3d-91df-92b657556ce9" width="250" alt="Offering Selection screen">
<img src="https://github.com/user-attachments/assets/98ac3c42-07b5-44e7-9164-9840d7c2eec1" width="250" alt="Offering Selection screen">
<img src="https://github.com/user-attachments/assets/3e71dc01-259a-4e68-9546-8b90ded294b0" width="250" alt="Offering Selection screen">
<img src="https://github.com/user-attachments/assets/c6cddd8f-3f01-47d3-9814-25028879968f" width="250" alt="Offering Selection screen">
<img src="https://github.com/user-attachments/assets/d6cf5fed-d3f8-4199-9582-1880f93a4982" width="250" alt="Offering Selection screen">

#### Payment Methods Flow
<img src="https://github.com/user-attachments/assets/ddb46299-172d-42b7-b76e-ae4cc10f4701" width="250" alt="Offering Selection screen">
<img src="https://github.com/user-attachments/assets/4d6d3b6e-e6aa-4deb-a213-1ee75d247449" width="250" alt="Offering Selection screen">
<img src="https://github.com/user-attachments/assets/26db6d2b-59c8-4c28-b4b4-0d25e2fe475f" width="250" alt="Offering Selection screen">
<img src="https://github.com/user-attachments/assets/6856f62c-d9a3-47d7-aefb-6b35d460650e" width="250" alt="Offering Selection screen">

#### Quote Details Flow
<img src="https://github.com/user-attachments/assets/232b5a00-7277-49e1-8c10-8a288efd990b" width="250" alt="Offering Selection screen">
<img src="https://github.com/user-attachments/assets/c65d7533-3af6-4f7a-b491-b9101b1b2b83" width="250" alt="Offering Selection screen">

#### Order Result
<img src="https://github.com/user-attachments/assets/613be950-8dd7-4499-83de-ca5865058d37" width="250" alt="Offering Selection screen">
<img src="https://github.com/user-attachments/assets/90fef355-483b-4ca6-bf0e-9bec3ab75010" width="250" alt="Offering Selection screen">

#### Order List and Detail
<img src="https://github.com/user-attachments/assets/eb46e888-256d-4f33-8e64-211c7151326d" width="250" alt="Offering Selection screen">
<img src="https://github.com/user-attachments/assets/95a4e897-fe3f-4b2e-a8e4-b329598b7b31" width="250" alt="Offering Selection screen">

#### Profile Screen
<img src="https://github.com/user-attachments/assets/8f48ec33-ef57-477e-9976-b75232e72377" width="250" alt="Offering Selection screen">



### App Flow Recording (#Click to view)
<a href="https://youtu.be/_Fh0by5D4Gw">
  <img src="https://github.com/user-attachments/assets/4cbe49fa-291e-4024-87dc-de4004cbd7d0" alt="Watch the video" width="250">
</a>



## Future Plans / Roadmap
1. Integration with More PFIs: Broaden our network of Participating Financial Institutions to offer more options to our users.

## Current Limitations and Known Issues
While we strive for excellence, FourthWall is still in active development. Here are some current limitations and known issues:

1. Large app size: The size of the app will be significantly reduced in subsequent versions once the Proguard rules for the TBDex SDK are obtained and code shrinking is performed.
2. Limited Currency Support: Currently, we support a limited number of currencies. We're working on expanding this list even as we onboard more PFIs.


## Technical Stack
This Android application leverages several key libraries and frameworks:

- Hilt: For dependency injection, simplifying the management of app components.
- Retrofit & OkHttp: Handle API communications and network requests.
- Room: Provides an abstraction layer over SQLite for local data persistence.
- Navigation Component: Manages in-app navigation and implements the single-activity architecture.
- Glide: Efficient image loading and caching.
- Gson: JSON parsing and serialization.
- Coroutines: Manage asynchronous tasks and background operations.
- ViewModel & LiveData: Implement MVVM architecture for robust and testable code.
- Biometrics: Integrate device authentication features.
- Lottie: Render high-quality animations.
- tbdex-httpclient & tbdex-protocol: Implement TBDex protocol for decentralized exchanges.

The project uses Kotlin and follows modern Android development practices, including Jetpack libraries and Material Design components.
## Getting Started

### Prerequisites

- Android Studio Arctic Fox or later
- JDK 11 or later
- Gradle 7.0 or later

### Installation

1. Clone the repository git clone https://github.com/judahben149/FourthWall.git
2. Open the project in Android Studio
3. Sync the project with Gradle files
4. Run the app on an emulator or physical device

## Contributing

We welcome contributions to FourthWall! Please follow these steps:

1. Fork the repository
2. Create a new branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct and the process for submitting pull requests.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

## Acknowledgements

- [TBDex Hackathon](https://www.tbdex.io/hackathon)
- [TBD Documentation](https://developer.tbd.website/docs/tbdex/wallet/overview/)
