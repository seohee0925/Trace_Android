# Trace 🐾
> 2024.01.04 ~ 2024.01.10 <br />
*for 2023 Winter Madcamp Week02 Project* <br/>
<p>
  <img alt="Android" src="https://img.shields.io/badge/Android-3DDC84.svg?&style=for-the badge&logo=Android&logoColor=white"/>
  <img alt="Spring" src="https://img.shields.io/badge/Spring-6DB33F.svg?&style=for-the badge&logo=Spring&logoColor=white"/>
  <img alt="MySQL" src="https://img.shields.io/badge/MySQL-4479A1.svg?&style=for-the badge&logo=MySQL&logoColor=white"/>
</p>

![image](https://github.com/seohee0925/Trace_Android/assets/102652293/05498026-d2fd-49e7-b95b-fdcf84d2edbe)

## 🛠️ 개발 환경
**FE**
- OS : Android
- IDE : Android Studio
- Language : Kotlin

**BE**
- Spring Boot
- MySQL
## 서비스 소개
![user scenario](https://github.com/seohee0925/Trace_Android/assets/102652293/37046df3-9bdf-4dae-ba52-ab888fab9d0e)

### 1️⃣ Sign up, Login
![Screen_Recording_20240110_191512_Trace](https://github.com/seohee0925/Trace_Android/assets/102652293/9bf3538c-1854-4f68-a725-6918fc7e2784) <br/>

**주요 기능**
- 이메일과 비밀번호, 이름을 통해 자체 회원가입, 로그인
- 네이버 소셜로 회원가입 및 로그인

**기술 설명**
- 서버에 post 방식으로 이메일, 비밀번호, 이름을 전송하여 member table에 저장됩니다.
- **네이버 sdk**를 이용하여 네이버에 저장된 정보를 받아와서 마이페이지에 저장됩니다.
- 네이버 아이디로 가입할 경우 별도의 로그인 절차 없이 바로 메인페이지로 이동합니다.

### 2️⃣ Main Page
<img src="https://github.com/seohee0925/Trace_Android/assets/102652293/efb8fef2-3caf-44a4-989c-44f956ac501c" width="300" height="700" />
<img src="https://github.com/seohee0925/Trace_Android/assets/102652293/7746edef-2d4c-4d3b-9c4a-f5c245e8d190" width="300" height="700" />
<img src="https://github.com/seohee0925/Trace_Android/assets/102652293/2f6c6988-6a79-41a6-8de3-c23067c2bb3c" width="300" height="700" />


**주요 기능**
- 현재 위치와 사진, 본문을 포스팅합니다.
- 지도 내에 나와 다른 사람들이 저장한 포스팅들이 마커로 표시되며,
20m 이내의 마커들의 내용을 확인할 수 있습니다.
- 제자리 버튼을 누르면 현재 위치로 돌아옵니다.

**기술 설명**
- **구글맵 API**를 이용하여 **사용자의 현 위치**를 불러옵니다.
- 글 작성시에 현재 위치와 주소, 작성자, 본문, 사진 값을 DB로 전송합니다.
- 사용자가 보고 있는 **화면 내에 위치하는 포스팅**들을 불러와 마커로 표시합니다.
    - 사용자가 보고 있는 화면이 업데이트되면, 사용자가 보고 있는 좌표를 DB에 요청합니다.
    - DB 내에서 컨트롤러를 활용하여 해당 **좌표 범위 내의 모든 포스팅**들을 불러와 클라이언트에게 **전송**합니다.
    - 클라이언트에서 사용자의 좌표와 포스팅이 **작성된 위치를 계산**합니다.
        - 작성된 위치가 20m 보다 멀리 떨어진 지점이라면, 터치시에 **거리 정보만를 표출**하는 마커를 생성합니다.
        - 작성된 위치가 20m 이내라면 터치시에 **작성자, 본문, 사진을 불러와 표시**합니다.
- 다른 탭으로 넘어가면 (화면에 지도가 표출되지 않으면) 리소스 소모를 막기 위해 지도 **업데이트를 중지**하고, 다시 지도로 돌아오면 **좌표를 업데이트**합니다.

### 3️⃣ My Page
![마이페이지_1](https://github.com/seohee0925/Trace_Android/assets/102652293/a62d0fab-5410-4379-8c3d-0c6123ba0e39)
![마이페이지_2](https://github.com/seohee0925/Trace_Android/assets/102652293/9a82b797-4aa5-43ba-ad01-dde030603206)


**주요 기능**
- 사용자가 작성한 글들을 최신순으로 볼 수 있습니다.
- 캘린더를 통해 날짜를 선택함에 따라 그 당일에 작성한 게시물들을 보여줍니다.
- 사용자는 원하지 않는 게시물을 언제든지 삭제할 수 있습니다.
- 삭제한 내용은 **DB에 전송되어 캘린더**에도 반영이 됩니다.

**기술 설명**
- Material Design Cardview를 **RecyclerView**를 기반으로 작성한 게시물들을 보여줍니다.
- Date Picker에서 날짜를 선택할 경우 날짜를 DB에 요청하여 그 날짜에 해당하는 게시물들을 보여줍니다.
---
## ERD
![image](https://github.com/seohee0925/Trace_Android/assets/102652293/44a69c9b-2653-4d52-a37b-5907ad4676ba)


## 🎨 Design System
### Color Scheme
![image](https://github.com/seohee0925/Trace_Android/assets/102652293/ce9c4dc5-f1ed-4eb8-8f57-704f860ca570)

🧭 타지를 여행하거나, 매일을 타지에서보다 모험적으로 살아가는 여러분의 **진취적인 태도를 담은 주황색**을 시그니처 컬러로 지정하였습니다. <br/>
🎨 디스플레이 상의 컬러의 자연스러움을 위해 black과 white를 새로 정의하여 사용하였습니다. 
### Font Family
![image](https://github.com/seohee0925/Trace_Android/assets/102652293/9fc05a52-eecd-4f61-af0c-057274420338)

구글에서 지원하는 공식 font들 중 여러분의 유쾌한 성격과 쾌활함을 담은 “Red Hat Mono”를 폰트로 사용하였습니다.
## 🧑‍🤝‍🧑 팀원
<table border="" cellspacing="0" cellpadding="0" width="100%">
 <tr width="100%">
  <td align="center">Seohee Yoon</a></td>
  <td align="center">Jaeyong Jung</a></td>
 </tr>
 <tr>
  <td  align="center"><a href="mailto:appleid21@sookmyung.ac.kr"><img src="https://github.com/cbg7144/Gardener/assets/102652293/4607f870-f17f-4b55-993f-e7d4700131e0" border="0" width="90px"></a></td>
  <td  align="center"><a href="mailto:kaijjy@kaist.ac.kr"><img src="https://github.com/seohee0925/Trace_Android/assets/102652293/82b3c744-40f6-45a7-a095-11402c3579684" border="0" width="90px"></a></td>
 </tr>
 <tr width="100%">
  <td  align="center"><a href="mailto:appleid21@sookmyung.ac.kr">appleid21@sookmyung.ac.kr</a></td>
  <td  align="center"><a href="mailto:kaijjy@kaist.ac.kr">kaijjy@kaist.ac.kr</a></td>
 </tr>
 <tr width="100%">
       <td  align="center"><p>Login, Signup</p><p>My Page</p></td>
       <td  align="center"><p>PM</p><p>Main Page: Map</p></td>
     </tr>
  </table>
