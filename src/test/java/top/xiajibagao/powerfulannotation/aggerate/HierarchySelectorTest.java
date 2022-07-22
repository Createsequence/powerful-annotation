package top.xiajibagao.powerfulannotation.aggerate;

import lombok.Getter;
import org.junit.Assert;
import org.junit.Test;
import top.xiajibagao.powerfulannotation.aggregate.Hierarchical;
import top.xiajibagao.powerfulannotation.aggregate.HierarchySelector;

public class HierarchySelectorTest {

	@Test
	public void chooseTest() {
		final HierarchySelector<TestHierarchy> selector = HierarchySelector.nearestAndOldestPriority();

		TestHierarchy annotation1 = new TestHierarchy(0, 0);
		TestHierarchy annotation2 = new TestHierarchy(0, 0);
		Assert.assertEquals(annotation1, selector.choose(annotation1, annotation2));

		annotation1 = new TestHierarchy(0, 1);
		annotation2 = new TestHierarchy(0, 0);
		Assert.assertEquals(annotation1, selector.choose(annotation1, annotation2));

		annotation1 = new TestHierarchy(1, 0);
		annotation2 = new TestHierarchy(0, 0);
		Assert.assertEquals(annotation2, selector.choose(annotation1, annotation2));
	}

	@Test
	public void nearestAndNewestPriorityTest() {
		final HierarchySelector<TestHierarchy> selector = HierarchySelector.nearestAndNewestPriority();

		TestHierarchy annotation1 = new TestHierarchy(0, 0);
		TestHierarchy annotation2 = new TestHierarchy(0, 0);
		Assert.assertEquals(annotation2, selector.choose(annotation1, annotation2));

		annotation1 = new TestHierarchy(0, 1);
		annotation2 = new TestHierarchy(0, 0);
		Assert.assertEquals(annotation2, selector.choose(annotation1, annotation2));

		annotation1 = new TestHierarchy(0, 0);
		annotation2 = new TestHierarchy(1, 0);
		Assert.assertEquals(annotation1, selector.choose(annotation1, annotation2));
	}

	@Test
	public void farthestAndOldestPriorityTest() {
		final HierarchySelector<TestHierarchy> selector = HierarchySelector.farthestAndOldestPriority();

		TestHierarchy annotation1 = new TestHierarchy(0, 0);
		TestHierarchy annotation2 = new TestHierarchy(0, 0);
		Assert.assertEquals(annotation1, selector.choose(annotation1, annotation2));

		annotation1 = new TestHierarchy(0, 1);
		annotation2 = new TestHierarchy(0, 0);
		Assert.assertEquals(annotation1, selector.choose(annotation1, annotation2));

		annotation1 = new TestHierarchy(0, 0);
		annotation2 = new TestHierarchy(1, 0);
		Assert.assertEquals(annotation2, selector.choose(annotation1, annotation2));
	}

	@Test
	public void farthestAndNewestPriorityTest() {
		final HierarchySelector<TestHierarchy> selector = HierarchySelector.farthestAndNewestPriority();

		TestHierarchy annotation1 = new TestHierarchy(0, 0);
		TestHierarchy annotation2 = new TestHierarchy(0, 0);
		Assert.assertEquals(annotation2, selector.choose(annotation1, annotation2));

		annotation1 = new TestHierarchy(0, 1);
		annotation2 = new TestHierarchy(0, 0);
		Assert.assertEquals(annotation2, selector.choose(annotation1, annotation2));

		annotation1 = new TestHierarchy(1, 0);
		annotation2 = new TestHierarchy(0, 0);
		Assert.assertEquals(annotation1, selector.choose(annotation1, annotation2));
	}

	@Getter
	private static class TestHierarchy implements Hierarchical {

		private final Object root;
		private final int verticalIndex;
		private final int horizontalIndex;

		public TestHierarchy(int verticalIndex, int horizontalIndex) {
			this.root = null;
			this.verticalIndex = verticalIndex;
			this.horizontalIndex = horizontalIndex;
		}

	}

}
