# StaffPickVideos

### Important notes before viewing running Project on device
1. MainActivity is located at root of project for convenience.
> I refactored the project to outline MVVM architecture using Google's data binding (UI-heavy-logic all separated into ViewModels directory).
	Network layer and models all now in Models directory. Activities, views, adapters, etc. all located in Views directory.
	Done to logically separate these components of the project, reduce coupling, and increase test-ability, mock-ability.
2. Please view ***Development Process*** section below to know how the development was executed

### Organization and Architectural Clarity

* A bug-free app that displays the Staff Picks list
	 > App is bug-free to the extent that it's been usability tested by myself and a friend.
* Architectural clarity
	 > MVVM using Google's data binding
* Software design that can scale
   	 > Object-oriented design to semantically separate classes, implement, extend, etc.
* Clear, __custom__, functional, and responsive UI
	 > UI is easy to navigate and informational. Network state is observed with a library. 

### Using **one** 3rd party reactive library
*  Observe changes in network state. 
	 > Network state is asynchronous in itself; so using reactive programming to observe this is very powerful.
	 As the app gets larger and larger, observing a change in network state is extremely scalable and manageable 
	 as you're able to print a message to the user when, for example, network connection goes from CONNECTED to DISONNECTING.
	 
### Adding new features and functionality to the app...
* See for yourself!
  > ![my version 1 compressed](https://user-images.githubusercontent.com/14288932/33238139-b53c76d2-d254-11e7-9227-d08eba254120.gif)
* Four Categories
  > User can not only view Staff Pick (Recent) videos, but also Premieres, best of Month, and best of Year.
	This was to mock Vimeo's web UX experience on the app; notice the 4 categories in this link:
  [Vimeo's staff picks](https://vimeo.com/channels/staffpicks)
     
* Infinite Scrolling
  > Pagination for scrolling. As user scrolling reaches bottom of any list, more Videos will be 
  loaded to view (think scrolling newsfeed on Facebook app).
  
* Watch Videos
  > User can click on each Card (item in the list) to watch them in device browser.

* Uniqueness
  > Recent videos can also be best of the Month. I made sure videos can only appear in one list.

* Animation
  > Sliding animation while scrolling; progress spinner for loading.

* Design
  > Custom-made views and UI design. Immersive (full-screen) and pleasant viewing experience.
  
# Process

  > Leverage my training in agile project management for the app to 
  work on tasks based on evolving priority.
  Three development iterations: Networking layer, Design, Refactoring/Documentation.
  
  Had milestones and groomed product backlog, if you're curious, you can view it here:
  [milestone + backlog.pdf](https://github.com/iAutoparkCars/ETFQuery/files/1502666/milestone.backlog.pdf)

* After understanding the problem, I immediately made wireframes to draft designs, 
   and got feedback from a couple of friends on the designs; they can be viewed here:
	[Designs 1 and 2.pdf](https://github.com/iAutoparkCars/ETFQuery/files/1502668/Designs.1.and.2.pdf)  

* 1st iteration Task Backlog:

	TO DO : 

		1. created my Vimeo app/client
		2. Used Vimeo library to generate auth token
		3. Learned how to authenticate and make GET requests with Vimeo's library because it used Retrofit
				-- found some things in the library that I'd like to change
						--add an example for working authentication
						--give additional clarity in the GET request example. It's async
						--Video.getPlayCount() always returns null; maybe a bug. Several posts have been made on 
						SO and Vimeo developer's forum			
		4.  Make custom views to display four lists. The trick was to put the RecyclerView into a fragment, then
			this fragment as part of PageViewer, then connect PageViewer to a TabLayout to move between lists.
		5. separated the download video/download images tasks into new classes 

* 2nd iteration Task Backlog:
	
	TO DO : 

		1. load image using URL and async task       DONE
		2. Make cards clickable(to watch video)      DONE             
		3A.make the network's state observable       DONE
			   tested in subway
		3B. infinite scrolling                       DONE
			   debug redundant network calls
			   reset load on hide last Card
		3C. Cache http response                      skipped
		3D. Cache Bitmaps with Picasso               skipped
		4. tweak UI; make it look amazing            DONE
			i.  opacity, color						 
			ii. fonts
			iii.max widths/heights ConstraintLayout
			iv. formatting data for display
			v.  data binding for views and async loading
			vi. loading progress bar + custom styling
			vii. app icon, display time, title   DONE
		5.  changing project root names              skipped
		6A. Vid uniqueness using HashSet             DONE 
		6B. pull-down refresh                        skipped
		6C. search function                          skipped
		7A. swipe to remove                          skipped
		7B. cache images                             skipped
		8.  onClick CardView animation               skipped
		9.  upload app to a free appstore            skipped
                ....
